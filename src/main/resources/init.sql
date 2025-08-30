-- Enable pgcrypto extension for UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE TOBUY (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       title VARCHAR(50) NOT NULL,
                       description TEXT,
                       criteria TEXT,
                       bought TIMESTAMP,
                       estimated_price INT,
                       interest VARCHAR(50)
);

CREATE TABLE TODO (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      title VARCHAR(50) NOT NULL,
                      description TEXT
);

CREATE TABLE TOBUY_CATEGORIES (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  name VARCHAR(50) NOT NULL,
                                  description TEXT NOT NULL,
                                  color VARCHAR(50) NOT NULL
);

CREATE TABLE TODO_CATEGORIES (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 name VARCHAR(50) NOT NULL,
                                 description TEXT NOT NULL,
                                 color VARCHAR(50) NOT NULL
);

CREATE TABLE TOBUY_LINKS (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             url VARCHAR(255) NOT NULL,
                             price SMALLINT,
                             favourite BOOLEAN NOT NULL,
                             tobuy_id UUID NOT NULL,
                             FOREIGN KEY (tobuy_id) REFERENCES TOBUY (id)
);

CREATE TABLE NONOGRAM_LOGS (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               started TIMESTAMP NOT NULL,
                               ended TIMESTAMP NOT NULL
);

CREATE TABLE BANNER_MESSAGES (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 message VARCHAR(150) NOT NULL,
                                 priority SMALLINT NOT NULL
);

CREATE TABLE TD_CONTAINS (
                             todo_id UUID NOT NULL,
                             category_id UUID NOT NULL,
                             PRIMARY KEY (todo_id, category_id),
                             FOREIGN KEY (todo_id) REFERENCES TODO (id),
                             FOREIGN KEY (category_id) REFERENCES TODO_CATEGORIES (id)
);

CREATE TABLE TB_CATEGORIES (
                               tobuy_id UUID NOT NULL,
                               category_id UUID NOT NULL,
                               PRIMARY KEY (tobuy_id, category_id),
                               FOREIGN KEY (tobuy_id) REFERENCES TOBUY (id),
                               FOREIGN KEY (category_id) REFERENCES TOBUY_CATEGORIES (id)
);

CREATE TABLE TD_HAS (
                        todo_id UUID NOT NULL,
                        subtask_id UUID NOT NULL,
                        PRIMARY KEY (todo_id, subtask_id),
                        FOREIGN KEY (todo_id) REFERENCES TODO (id),
                        FOREIGN KEY (subtask_id) REFERENCES TODO (id)
);
