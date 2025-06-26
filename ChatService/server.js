// server.js - The main entry point

// Load environment variables
require('dotenv').config();

const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors'); // It's good practice to use the cors package

// --- Import our modularized components ---
const connectDB = require('./config/database');
const socketAuthMiddleware = require('./middleware/socketAuth');
const initializeSocket = require('./sockets/socketHandler');
const mainRoutes = require('./routes/index');

// --- Initializations ---
const app = express();
const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: "*", // For development. Be more specific in production.
        methods: ["GET", "POST"]
    }
});

// --- Connect to Database ---
connectDB();

// --- Middlewares ---
app.use(cors()); // Use CORS for Express routes
app.use(express.json()); // To parse JSON bodies for future API endpoints

// --- API Routes ---
app.use('/', mainRoutes);

// --- Socket.IO setup ---
io.use(socketAuthMiddleware); // Apply our custom auth middleware to Socket.IO
initializeSocket(io);       // Pass the `io` instance to our socket handler

// --- Run the server ---
const PORT = process.env.PORT || 4000;
server.listen(PORT, () => {
    console.log(`🚀 Server is listening on port ${PORT}`);
});