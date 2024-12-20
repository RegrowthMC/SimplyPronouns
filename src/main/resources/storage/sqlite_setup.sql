CREATE TABLE IF NOT EXISTS simplypronouns_users
(
    uuid BINARY(128) NOT NULL,
    username CHAR(17),
    pronouns TEXT,
    PRIMARY KEY (uuid)
);
|
CREATE TABLE IF NOT EXISTS simplypronouns_pronouns
(
    pronoun TEXT NOT NULL,
    status VARCHAR(32),
    PRIMARY KEY (pronoun)
);