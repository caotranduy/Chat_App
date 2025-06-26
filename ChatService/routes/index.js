// routes/index.js

const express = require('express');
const router = express.Router();

// Simple API route to check if the server is running
router.get('/', (req, res) => {
    res.send('<h1>Chat Server is ready! 🚀</h1>');
});

// You can add more routes here in the future
// router.get('/api/users', ...);

module.exports = router;