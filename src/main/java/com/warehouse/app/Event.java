
package com.warehouse.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Event {
	@Id
	Long id;
	@Column
	String sku;
	@Column
	String type;
	@Column
	String payload;
	@Column
	String createdAt;
}
