// Logique metier : les notifications sont stockees en memoire (perdues a l'arret du service).
const notifications = [];
let nextId = 1;

function findAll() {
  return notifications;
}

function create(memberId, message) {
  const notification = {
    id: nextId++,
    memberId,
    message,
    createdAt: new Date().toISOString(),
  };
  notifications.push(notification);
  return notification;
}

module.exports = { findAll, create };
