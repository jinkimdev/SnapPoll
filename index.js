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

// when testing with local node server and local postgres db,
// enter before running node: 
// $ export DATABASE_URL=postgres:///$(whoami)
var DEBUG = 1;

/* ################ REST API ################ */

/* -------- DEBUG -------- */

router.get('/', function(req, res) {
	res.send('Snap Poll REST API');
	if (DEBUG) {
		console.log("DEBUG MODE");
	}
});

router.get('/debug', function(req, res) {
	if (DEBUG) console.log(process.env.DATABASE_URL);
	res.send('Hello World!');
});

router.get('/test', function(req, res) {
	res.send("TEST 33");
});


/* -------- POLL -------- */

// GET - retrieve all polls in the table
router.get('/poll', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var q = squel.select()
			.from('polls')
			.toString();
		client.query(q, function(err, result) {
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
		var q = squel.select().from('polls')
			.where("poll_id='" + req.params.poll_id + "'")
			.toString();
		client.query(q, function(err, result) {
			done();
			if (err) {
				console.error(err);
				res.send("Error " + err);
			} else {
				res.send(result.rows[0]);
			}
		});
	});
});

// POST - insert a poll created by a user (poll creator)
router.post('/poll', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var q = squel.insert().into('polls')
			.set('creator_id', req.body.creator_id)
			.set('q_text', req.body.q_text)
			.set('allow_multiple_responses', req.body.allow_multiple_responses)
			.toString();
		client.query(q, function(err, result) {
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
		var q = squel.delete().from('polls')
			.where('poll_id=' + req.body.poll_id)
			.toString();
		client.query(q, function(err, result) {
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
		var q = squel.insert().into('users')
			.set('user_id', req.body.user_id)
			.set('f_name', req.body.f_name)
			.set('l_name', req.body.l_name)
			.toString();
		client.query(q, function(err, result) {
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
		var q = squel.update().table('users')
			.set('f_name', req.body.f_name)
			.set('l_name', req.body.l_name)
			.toString();
		client.query(q, function(err, result) {
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
		var q = squel.select().from('responses')
			.where("response_id='" + req.params.response_id + "'")
			.toString();
		client.query(q, function(err, result) {
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
		var q = squel.insert().into('responses')
			.set('x', req.body.x)
			.set('y', req.body.y)
			.set('respondent_id', req.body.respondent_id)
			.set('attribute_choice', req.body.attribute_choice)
			.set('poll_id', req.body.poll_id)
			.toString();
		client.query(q, function(err, result) {
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
		var q = squel.update().table('respondes')
			.set('x', req.body.x)
			.set('y', req.body.y)
			.set('respondent_id', req.body.respondent_id)
			.set('attribute_choice', req.body.attribute_choice)
			.set('poll_id', req.body.poll_id)
			.toString();
		client.query(q, function(err, result) {
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
router.get('/result/:poll_id', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var q = squel.select().from('responses')
			.where("poll_id=" + req.params.poll_id)
			.toString();
		client.query(q, function(err, result) {
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
