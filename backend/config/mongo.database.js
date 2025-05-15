// config/database.js
const mongoose = require('mongoose');

const connectDB = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI || 'mongodb://localhost:27017/ChatApp', {
      useNewUrlParser: true,
      useUnifiedTopology: true,
      // useCreateIndex: true, // tùy thuộc vào phiên bản Mongoose của bạn
      // useFindAndModify: false, // tùy thuộc vào phiên bản Mongoose của bạn
    });
    console.log('MongoDB connected');
  } catch (error) {
    console.error('MongoDB connection error:', error);
    process.exit(1); // Thoát tiến trình nếu không kết nối được với database
  }
};

module.exports = connectDB;