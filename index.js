var express = require('express');
var app = express();
var pg = require('pg');
var bodyParser = require('body-parser');
var router = express.Router();
var squel = require('squel');

// parse urlencoded request bodies into req.body
app.use(bodyParser.urlencoded({	extended: true }));
app.use(bodyParser.json());

app.set('port', (process.env.PORT || 5000));
app.use(express.static(__dirname + '/public'));

// router setup
router.get('/', function(req, res, next) {
	next();
})

/* -------- REST API -------- */

/* -------- DEBUG -------- */

router.get('/', function(req, res) {
	console.log(process.env.DATABASE_URL);
	res.send('Snap Poll REST API')
});

router.get('/debug', function(req, res) {
	console.log(process.env.DATABASE_URL);
	res.send('Hello World!')
});

router.get('/test', function(req, res) {
	res.send("TEST 33");
});

router.get('/db', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString = "SELECT * FROM test_table";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

/* -------- POLL -------- */

// GET - retrieve all polls in the table
router.get('/poll', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		// console.log("#### pg.connect #### " + client);
		// var queryString = "SELECT * FROM polls";
		var queryString = squel.select().from('polls').toString();
		
		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

// GET - retrieve a poll with the passed in poll_id
router.get('/poll/:poll_id', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString = "SELECT * FROM polls WHERE poll_id=" + req.params.poll_id + ";";
		
		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

// POST - insert a poll created by a user (poll creator)
router.post('/poll', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "INSERT INTO polls (creator_id, q_text, allow_multiple_responses) VALUES ('"
				+ req.body.creator_id + "', '" 
				+ req.body.question + "', '"
				+ req.body.allow_multiple_responses + "');";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

// DELETE - delete a poll specified the poll_id
router.delete('/poll', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "DELETE FROM polls WHERE poll_id='"
				+ req.body.poll_id + "';";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

/* -------- USER -------- */

// create a user, user_id should be unique
router.post('/user', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "INSERT INTO users (user_id, f_name, l_name) VALUES ('"
				+ req.body.user_id + "', '" 
				+ req.body.f_name + "', '"
				+ req.body.l_name + "');";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

// PUT - update user info
router.put('/user', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "UPDATE users SET f_name='" + req.body.f_name
				+ "', l_name='" + req.body.l_name + "'" 
				+ " WHERE user_id='" + req.body.user_id + "';";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

/* -------- RESPONSE -------- */

// GET - retrieve a response specified by response_id
router.get('/response/:response_id', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "SELECT * FROM responses WHERE response_id='"
				+ req.body.response_id + "';";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

// POST - insert a response to a poll
router.post('/response', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "INSERT INTO responses (x, y, respondent_id, attribute_choice, poll_id)"
				+ " VALUES ('"
				+ req.body.x + "', '"
				+ req.body.y + "', '"
				+ req.body.respondent_id + "', '"
				+ req.body.attribute_choice + "', '"
				+ req.body.poll_id + "';";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

// PUT - update a response specified by response_id
router.put('/response', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "UPDATE responses SET x='" + req.body.x + "', "
				+ "y='" + req.body.y + "', "
				+ "respondent_id='" + req.body.respondent_id + "', "
				+ "attribute_choice='" + req.body.attribute_choice + "' "
				+ "WHERE response_id='" + req.body.response_id + "';";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

// DELETE - delete a response specified by response_id

/* -------- RESULT -------- */

// GET - retrieve all responses to the specified poll_id
router.get('/result', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString
			= "SELECT * FROM responses WHERE poll_id='"
				+ req.body.poll_id + "';";

		client.query(queryString, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows);
			}
		});
	});
});

/* ---------------------------------------------------------------- */

app.use('/api', router);
app.listen(app.get('port'), function() {
	console.log("Node app is running at localhost:" + app.get('port'))
});
