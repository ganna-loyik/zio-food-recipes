CREATE TABLE IF NOT EXISTS tags(
  id BIGSERIAL PRIMARY KEY, 
  name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS ingredients(
  id BIGSERIAL PRIMARY KEY, 
  name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS recipes(
  id BIGSERIAL PRIMARY KEY, 
  name TEXT NOT NULL,
  description TEXT,
  instructions TEXT NOT NULL,
  preparation_time INTEGER NOT NULL CHECK (preparation_time >= 0),
  waiting_time INTEGER NOT NULL CHECK (waiting_time >= 0)
);

CREATE TYPE ingredient_unit AS ENUM ('Gram', 'Milliliter');

CREATE TABLE IF NOT EXISTS recipe2ingredients(
  recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
  ingredient_id BIGINT NOT NULL REFERENCES ingredients(id) ON DELETE CASCADE,
  amount INTEGER NOT NULL CHECK (amount > 0),
  unit ingredient_unit NOT NULL,
  PRIMARY KEY(recipe_id, ingredient_id)
);

CREATE TABLE IF NOT EXISTS recipe2tags(
  recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
  tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
  PRIMARY KEY(recipe_id, tag_id)
);

INSERT INTO ingredients(name) VALUES ('flour'), ('water'), ('apple'), ('salt'), ('egg');

INSERT INTO tags(name) VALUES ('meat'), ('breakfast'), ('drink');