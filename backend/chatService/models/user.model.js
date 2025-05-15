// models/user.model.js
const { useDebugValue } = require('react');
const pool = require('../config/mysql.database');
const bcrypt = require('bcrypt');

class User {
  constructor(id, username, password, email, createdAt, updatedAt) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  static async findOne(condition) {
    let query = 'SELECT * FROM users WHERE ';
    const values = [];

    if (condition.username) {
      query += 'username = ?';
      values.push(condition.username);
    } else if (condition.email) {
      query += 'email = ?';
      values.push(condition.email);
    } else {
      return null;
    }

    const [rows] = await pool.execute(query, values);
    if (rows.length > 0) {
      const user = rows[0];
      return new User(user.id, user.username, user.password, user.email, user.createdAt, user.updatedAt);
    }
    return null;
  }

  static async findById(id) {
    const [rows] = await pool.execute('SELECT * FROM users WHERE id = ?', [id]);
    if (rows.length > 0) {
      const user = rows[0];
      return new User(user.id, user.username, user.password, user.email, user.createdAt, user.updatedAt);
    }
    return null;
  }

  static async create(username, password, email) {
    const hashedPassword = await bcrypt.hash(password, 10);
    const [result] = await pool.execute(
      'INSERT INTO users (username, password, email, createdAt, updatedAt) VALUES (?, ?, ?, NOW(), NOW())',
      [username, hashedPassword, email]
    );
    const userId = result.insertId;
    return new User(userId, username, hashedPassword, email, new Date(), new Date());
  }

  async comparePassword(candidatePassword) {
    return await bcrypt.compare(candidatePassword, this.password);
  }
}

module.exports = User;