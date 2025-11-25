-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
-- Host: localhost    Database: dut-video2gif

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password_hash` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--
LOCK TABLES `users` WRITE;
INSERT INTO `users` VALUES (1,'user01','123456','user01@example.com'); -- CHÚ Ý: Cần thay '123456' bằng hash thực tế
UNLOCK TABLES;

--
-- Table structure for table `video_requests`
--

DROP TABLE IF EXISTS `video_requests`;
CREATE TABLE `video_requests` (
  `request_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT DEFAULT NULL,
  `original_video_name` VARCHAR(255) DEFAULT NULL,
  `video_path` VARCHAR(500) DEFAULT NULL,
  `gif_path` VARCHAR(500) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'PENDING',
  `start_time` VARCHAR(10) DEFAULT '00:00',
  `end_time` VARCHAR(10) DEFAULT '00:00',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`request_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Table structure for table `jobs` - Sẽ không sử dụng cho mô hình TCP, nhưng giữ lại để theo dõi hàng đợi nếu cần.
-- Trong mô hình TCP, chúng ta chỉ cần một bảng `video_requests` để theo dõi trạng thái.
--

DROP TABLE IF EXISTS `jobs`;
CREATE TABLE `jobs` (
  `job_id` INT NOT NULL AUTO_INCREMENT,
  `request_id` INT DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'PENDING',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`job_id`),
  FOREIGN KEY (`request_id`) REFERENCES `video_requests` (`request_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;