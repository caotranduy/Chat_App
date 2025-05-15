// server.js
const express = require('express');
const connectMySQL = require('./config/mysql.database'); // Import để kiểm tra kết nối MySQL
const authRoutes = require('./routes/auth.routes');
//const chatRoutes = require('./routes/chat.routes'); // Import routes chat
const dotenv = require('dotenv');
const path = require('path');

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());

const rootDir = path.join(__dirname, '..'); // Đường dẫn đến thư mục chatapp

// Để trỏ đến thư mục frontendWEB
const frontendDir = path.join(rootDir, 'frontendWEB');
app.use(express.static(frontendDir));
app.use('/css', express.static(path.join(frontendDir,'css')));
app.use('/js', express.static(path.join(frontendDir,'js')));
app.use('/lang', express.static(path.join(frontendDir,'lang')));

app.get(['/', '/login', '/register'], (req, res) => {
    res.sendFile(path.join(frontendDir, 'index.html'));
});

app.use('/api/auth', authRoutes);
//app.use('/api/chat', chatRoutes); // Sử dụng routes chat

async function initialize() {
    try {
        await connectMySQL.getConnection();
        console.log('MySQL connected for users');
    } catch (error) {
        console.error('Failed to connect to MySQL:', error);
        process.exit(1);
    }
}

initialize();

app.listen(PORT, () => {
    console.log(`Server đang chạy trên cổng ${PORT}`);
});

console.log(__dirname);
console.log(frontendDir);