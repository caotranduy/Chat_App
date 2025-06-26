const mongoose = require('mongoose');
const Schema = mongoose.Schema;

/**
 * Schema for a message.
 * - conversationId: The ID of the conversation this message belongs to.
 * - senderId: The ID of the user who sent the message.
 * - content: The text content of the message.
 */
const messageSchema = new Schema({
    conversationId: {
        type: Schema.Types.ObjectId,
        ref: 'Conversation',
        required: true
    },
    senderId: {
        type: String,
        ref: 'User',
        required: true
    },
    content: {
        type: String,
        required: true
    }
}, { timestamps: true });

module.exports = mongoose.model('Message', messageSchema);