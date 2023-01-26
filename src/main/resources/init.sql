CREATE TABLE IF NOT EXISTS tags(
  id BIGSERIAL PRIMARY KEY, 
  name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS ingridients(
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

CREATE TYPE ingridient_unit AS ENUM ('Gram', 'Milliliter');

CREATE TABLE IF NOT EXISTS recipe2ingridients(
  recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
  ingridient_id BIGINT NOT NULL REFERENCES ingridients(id) ON DELETE CASCADE,
  amount INTEGER NOT NULL CHECK (amount > 0),
  unit ingridient_unit NOT NULL,
  PRIMARY KEY(recipe_id, ingridient_id)
);

CREATE TABLE IF NOT EXISTS recipe2tags(
  recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
  tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
  PRIMARY KEY(recipe_id, tag_id)
);

INSERT INTO ingridients(name) VALUES ('flour'), ('water'), ('apple'), ('salt'), ('egg');

INSERT INTO tags(name) VALUES ('meat'), ('breakfast'), ('drink');