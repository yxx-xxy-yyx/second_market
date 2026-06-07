/**
 * 日志工具类
 * 用于在开发环境输出调试信息，在生产环境自动禁用
 */

// 判断是否为生产环境
const isProduction = import.meta.env.PROD;

// 日志级别
const LOG_LEVELS = {
  DEBUG: 0,
  INFO: 1,
  WARN: 2,
  ERROR: 3
};

// 当前日志级别（开发环境默认 INFO，生产环境 ERROR）
const currentLogLevel = isProduction ? LOG_LEVELS.ERROR : LOG_LEVELS.INFO;

const logger = {
  debug: (...args) => {
    if (!isProduction && currentLogLevel <= LOG_LEVELS.DEBUG) {
      console.log('[DEBUG]', ...args);
    }
  },
  
  info: (...args) => {
    if (!isProduction && currentLogLevel <= LOG_LEVELS.INFO) {
      console.log('[INFO]', ...args);
    }
  },
  
  warn: (...args) => {
    if (currentLogLevel <= LOG_LEVELS.WARN) {
      console.warn('[WARN]', ...args);
    }
  },
  
  error: (...args) => {
    if (currentLogLevel <= LOG_LEVELS.ERROR) {
      console.error('[ERROR]', ...args);
    }
  },
  
  // 始终输出，不受环境限制
  log: (...args) => {
    console.log(...args);
  }
};

export default logger;
