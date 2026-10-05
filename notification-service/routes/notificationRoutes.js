const express = require('express');
const notificationService = require('../services/notificationService');

const router = express.Router();

// GET /api/notifications/hello
router.get('/hello', (req, res) => {
  res.json({ message: 'Hello from Notification Service (Node.js + Express)' });
});

// GET /api/notifications  (filtre optionnel : ?memberId=1)
router.get('/', (req, res) => {
  let list = notificationService.findAll();
  if (req.query.memberId !== undefined) {
    list = list.filter((n) => String(n.memberId) === String(req.query.memberId));
  }
  res.json(list);
});

// POST /api/notifications  { "memberId": 1, "message": "..." }
router.post('/', (req, res) => {
  const { memberId, message } = req.body || {};
  if (memberId === undefined || memberId === null || typeof message !== 'string' || message.trim() === '') {
    return res.status(400).json({
      status: 400,
      error: 'Bad Request',
      message: 'memberId et message sont obligatoires',
    });
  }
  const notification = notificationService.create(memberId, message.trim());
  console.log(`[Notification #${notification.id}] membre ${notification.memberId} : ${notification.message}`);
  res.status(201).json(notification);
});

module.exports = router;
