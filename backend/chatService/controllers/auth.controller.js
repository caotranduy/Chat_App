// controllers/auth.controller.js
const User = require('../models/user.model');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

// Hàm tạo JWT
const generateToken = (userId) => {
  return jwt.sign({ id: userId }, process.env.JWT_SECRET || 'your-secret-key', { expiresIn: '1h' }); // Thay 'your-secret-key' bằng khóa bí mật thực tế của bạn và điều chỉnh thời gian hết hạn
};

// Đăng ký người dùng mới
exports.register = async (req, res) => {
  try {
    const { username, password, email } = req.body;

    // Kiểm tra xem người dùng đã tồn tại chưa
    const existingUser = await User.findOne({ $or: [{ username }, { email }] });
    if (existingUser) {
      return res.status(400).json({ message: 'Tên người dùng hoặc email đã tồn tại' });
    }

    const newUser = new User({ username, password, email });
    await newUser.save();

    res.status(201).json({ message: 'Đăng ký thành công' });
  } catch (error) {
    console.error('Lỗi đăng ký:', error);
    res.status(500).json({ message: 'Đã có lỗi xảy ra khi đăng ký' });
  }
};

// Đăng nhập người dùng
exports.login = async (req, res) => {
  try {
    const { username, password } = req.body;

    // Tìm người dùng theo tên người dùng
    const user = await User.findOne({ username });
    if (!user) {
      return res.status(401).json({ message: 'Tên người dùng hoặc mật khẩu không đúng' });
    }

    // So sánh mật khẩu
    const isPasswordValid = await user.comparePassword(password);
    if (!isPasswordValid) {
      return res.status(401).json({ message: 'Tên người dùng hoặc mật khẩu không đúng' });
    }

    // Tạo JWT token
    const token = generateToken(user._id);

    res.status(200).json({ token, message: 'Đăng nhập thành công' });
  } catch (error) {
    console.error('Lỗi đăng nhập:', error);
    res.status(500).json({ message: 'Đã có lỗi xảy ra khi đăng nhập' });
  }
};