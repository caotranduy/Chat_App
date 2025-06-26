// utils/jwtUtils.js

const jwt = require('jsonwebtoken');

/**
 * Verifies a JWT token, ensuring compatibility with Spring's Base64 encoded secret.
 * @param {string} token The JWT token to verify.
 * @returns {object} The decoded payload of the token if valid.
 * @throws {Error} Throws an error if the token is invalid or expired.
 */
const verifyToken = (token) => {
    try {
        // This is the crucial step to match Spring's logic.
        // We decode the Base64 secret from .env before using it for verification.
        const decodedSecret = Buffer.from(process.env.JWT_SECRET, 'base64');

        const decoded = jwt.verify(token, decodedSecret);
        return decoded;
    } catch (err) {
        // Log the specific error for debugging and re-throw a generic one.
        console.error('JWT Verification Failed:', err.message);
        throw new Error('Invalid or expired token.');
    }
};

module.exports = {
    verifyToken,
};