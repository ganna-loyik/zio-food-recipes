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

CREATE TABLE IF NOT EXISTS public.event_journal(
  ordering BIGSERIAL,
  persistence_id VARCHAR(255) NOT NULL,
  sequence_number BIGINT NOT NULL,
  deleted BOOLEAN DEFAULT FALSE NOT NULL,

  writer VARCHAR(255) NOT NULL,
  write_timestamp BIGINT,
  adapter_manifest VARCHAR(255),

  event_ser_id INTEGER NOT NULL,
  event_ser_manifest VARCHAR(255) NOT NULL,
  event_payload BYTEA NOT NULL,

  meta_ser_id INTEGER,
  meta_ser_manifest VARCHAR(255),
  meta_payload BYTEA,

  PRIMARY KEY(persistence_id, sequence_number)
);

CREATE UNIQUE INDEX event_journal_ordering_idx ON public.event_journal(ordering);

CREATE TABLE IF NOT EXISTS public.event_tag(
    event_id BIGINT,
    tag VARCHAR(256),
    PRIMARY KEY(event_id, tag),
    CONSTRAINT fk_event_journal
      FOREIGN KEY(event_id)
      REFERENCES event_journal(ordering)
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.snapshot (
  persistence_id VARCHAR(255) NOT NULL,
  sequence_number BIGINT NOT NULL,
  created BIGINT NOT NULL,

  snapshot_ser_id INTEGER NOT NULL,
  snapshot_ser_manifest VARCHAR(255) NOT NULL,
  snapshot_payload BYTEA NOT NULL,

  meta_ser_id INTEGER,
  meta_ser_manifest VARCHAR(255),
  meta_payload BYTEA,

  PRIMARY KEY(persistence_id, sequence_number)
);

CREATE TABLE IF NOT EXISTS public.durable_state (
    global_offset BIGSERIAL,
    persistence_id VARCHAR(255) NOT NULL,
    revision BIGINT NOT NULL,
    state_payload BYTEA NOT NULL,
    state_serial_id INTEGER NOT NULL,
    state_serial_manifest VARCHAR(255),
    tag VARCHAR,
    state_timestamp BIGINT NOT NULL,
    PRIMARY KEY(persistence_id)
    );

CREATE INDEX state_tag_idx on public.durable_state (tag);
CREATE INDEX state_global_offset_idx on public.durable_state (global_offset);