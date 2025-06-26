// sockets/socketHandler.js

const Message = require('../models/Message');

// This function will receive the `io` instance from server.js
const initializeSocket = (io) => {

    io.on('connection', (socket) => {
        console.log(`🙋‍♂️ A user connected: ${socket.id}, UserID: ${socket.user.id}`);

        // Each user joins a private room with their own ID.
        socket.join(socket.user.id);

        // Event for when a user sends a message
        socket.on('sendMessage', async (data) => {
            const { conversationId, content, recipientId } = data;
            const senderId = socket.user.id;

            try {
                const newMessage = new Message({
                    conversationId,
                    senderId,
                    content
                });
                await newMessage.save();

                const room = recipientId || conversationId;
                io.to(room).emit('receiveMessage', {
                    ...newMessage.toObject(),
                    sender: socket.user
                });

                console.log(`[${conversationId}] Message from ${senderId} to ${room}`);

            } catch (error) {
                console.error('Error sending message:', error);
                socket.emit('sendMessageError', { message: 'Could not send message.' });
            }
        });

        // Event for joining a conversation (room)
        socket.on('joinConversation', (conversationId) => {
            socket.join(conversationId);
            console.log(`User ${socket.user.id} joined room ${conversationId}`);
        });

        // Event for when a user disconnects
        socket.on('disconnect', () => {
            console.log(`👋 User disconnected: ${socket.id}`);
        });
    });
};

module.exports = initializeSocket;