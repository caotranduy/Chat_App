const mongoose = require('mongoose');
const Schema = mongoose.Schema;

/**
 * Schema for a conversation.
 * - participants: Array containing the IDs of the participants.
 * - type: 'private' for 1-on-1 chats, 'group' for group chats.
 * - groupName: Name of the group chat (if it's a group).
 */
const conversationSchema = new Schema({
    participants: [{
        type: Schema.Types.ObjectId,
        ref: 'User' 
    }],
    type: {
        type: String,
        enum: ['private', 'group'],
        default: 'private'
    },
    groupName: {
        type: String,
        default: null
    },
    lastMessage: {
        type: Schema.Types.ObjectId,
        ref: 'Message'
    }
}, { timestamps: true });

module.exports = mongoose.model('Conversation', conversationSchema);