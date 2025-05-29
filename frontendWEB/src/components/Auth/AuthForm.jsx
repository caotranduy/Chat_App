import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const AuthForm = ({ isLogin }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    ...(isLogin ? {} : { name: '' })
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const url = isLogin 
        ? `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_LOGIN_URL}`
        : `${process.env.REACT_APP_API_BASE_URL}${process.env.REACT_APP_REGISTER_URL}`;

      const response = await axios.post(url, formData);
      
      // Lưu token nếu là đăng nhập
      if (isLogin) {
        localStorage.setItem('token', response.data.token);
        navigate('/dashboard'); // Chuyển hướng sau khi đăng nhập thành công
      } else {
        navigate('/login'); // Chuyển hướng đến trang đăng nhập sau khi đăng ký
      }
    } catch (err) {
      setError(err.response?.data?.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-form">
      <h2>{isLogin ? 'Login' : 'Register'}</h2>
      {error && <div className="error">{error}</div>}
      <form onSubmit={handleSubmit}>
        {!isLogin && (
          <div>
            <label>Name:</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
        )}
        <div>
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label>Password:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            minLength="6"
          />
        </div>
        <button type="submit" disabled={loading}>
          {loading ? 'Processing...' : isLogin ? 'Login' : 'Register'}
        </button>
      </form>
    </div>
  );
};

export default AuthForm;