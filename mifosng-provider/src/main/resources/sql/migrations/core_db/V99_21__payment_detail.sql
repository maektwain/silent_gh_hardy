SET foreign_key_checks = 0;
ALTER TABLE `m_payment_detail`
	ADD INDEX `m_payment_detail_m_payment_to_whom` (`payment_to_whom_id`),
	ADD CONSTRAINT `m_payment_detail_m_payment_to_whom` FOREIGN KEY (`payment_to_whom_id`) REFERENCES `m_payment_to_whom` (`id`);