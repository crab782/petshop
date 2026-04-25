CREATE TABLE pets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    pet_type VARCHAR(255) NOT NULL,
    breed VARCHAR(255),
    age INT,
    gender VARCHAR(50),
    avatar VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_pets_user_id ON pets(user_id);
CREATE INDEX idx_pets_name ON pets(name);
CREATE INDEX idx_pets_pet_type ON pets(pet_type);
