CREATE TABLE IF NOT EXISTS simplypronouns_users
(
    uuid CHAR(36) NOT NULL,
    pronounsId INTEGER NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS simplypronouns_pronouns
(
    id INTEGER NOT NULL,
    pronouns TEXT NOT NULL,
    customFormat TEXT,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS simplypronouns_requests
(
    pronouns TEXT NOT NULL,
    disabled BOOLEAN,
    PRIMARY KEY (pronouns)
);