const express = require('express');
const notificationRoutes = require('./routes/notificationRoutes');

const PORT = process.env.PORT || 8084;
const app = express();
const { Eureka } = require('eureka-js-client');

app.use(express.json());
app.get('/health', (req, res) => res.json({ status: 'UP' }));
app.use('/api/notifications', notificationRoutes);

// Route inconnue -> 404 JSON
app.use((req, res) => {
  res.status(404).json({ status: 404, error: 'Not Found', message: `Route introuvable : ${req.method} ${req.originalUrl}` });
});

// Gestion des erreurs (ex : JSON invalide -> 400)
// eslint-disable-next-line no-unused-vars
app.use((err, req, res, next) => {
  const status = err.status || 500;
  res.status(status).json({
    status,
    error: status === 400 ? 'Bad Request' : 'Internal Server Error',
    message: status === 400 ? 'Corps de la requete JSON invalide' : 'Erreur interne du serveur',
  });
});

app.listen(PORT, () => {
  console.log(`Notification Service demarre sur http://localhost:${PORT}`);
}
)
const eurekaClient = new Eureka({
  instance: {
    app: 'NOTIFICATION-SERVICE',
    instanceId: `notification-service:${PORT}`,
    hostName: 'localhost',
    ipAddr: '127.0.0.1',
    port: { '$': Number(PORT), '@enabled': true },
    vipAddress: 'notification-service',
    statusPageUrl: `http://localhost:${PORT}/health`,
    healthCheckUrl: `http://localhost:${PORT}/health`,
    dataCenterInfo: {
      '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
      name: 'MyOwn',
    },
  },
  eureka: {
    host: process.env.EUREKA_HOST || 'localhost',
    port: 8761,
    servicePath: '/eureka/apps/',
    fetchRegistry: false,
  },
});

eurekaClient.start((err) =>
    console.log(err ? 'Erreur Eureka : ' + err : 'Enregistre dans Eureka'));

process.on('SIGINT', () => eurekaClient.stop(() => process.exit()));
