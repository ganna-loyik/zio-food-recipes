CREATE TABLE IF NOT EXISTS recipes(
    id serial PRIMARY KEY, 
    name TEXT NOT NULL,
    description TEXT
);