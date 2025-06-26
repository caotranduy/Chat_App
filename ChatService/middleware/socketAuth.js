// middleware/socketAuth.js

const { verifyToken } = require('../utils/jwtUtils'); // Import our new utility

const socketAuthMiddleware = (socket, next) => {
    const token = socket.handshake.auth.token || socket.handshake.query.token;

    if (!token) {
        return next(new Error('Authentication error: Token not found.'));
    }

    try {
        // Use our centralized verification function
        const decodedPayload = verifyToken(token);
        
        // Attach the decoded user information to the socket object
        socket.user = decodedPayload;
        next();
    } catch (error) {
        // The error message comes from our utility function
        return next(new Error('Authentication error: ' + error.message));
    }
};

module.exports = socketAuthMiddleware;