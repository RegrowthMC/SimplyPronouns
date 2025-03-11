CREATE TABLE IF NOT EXISTS simplypronouns_users
(
    uuid CHAR(36) NOT NULL,
    username TEXT,
    pronouns TEXT,
    preferred_name TEXT,
    PRIMARY KEY (uuid)
);
|
CREATE TABLE IF NOT EXISTS simplypronouns_pronouns
(
    pronoun TEXT NOT NULL,
    status TEXT,
    PRIMARY KEY (pronoun)
);