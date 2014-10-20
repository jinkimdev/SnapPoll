var express = require('express');
var app = express();
var pg = require('pg');
var bodyParser = require('body-parser');

// parse urlencoded request bodies into req.body
app.use(bodyParser.urlencoded({	extended: true }));
app.use(bodyParser.json());

app.set('port', (process.env.PORT || 5000));
app.use(express.static(__dirname + '/public'));


/* -------- REST API -------- */

/* ######## DEBUG ######## */

app.get('/api', function(req, res) {
	res.send('Hello World!')
});

app.get('/api/test', function(req, res) {
	res.send("TEST 202");
});

app.get('/api/db', function(req, res) {
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

/* ######## POLL ######## */

app.get('/api/poll', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString = "SELECT * FROM poll";
		
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

app.get('/api/poll/:id', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString = "SELECT * FROM poll WHERE poll_id=" + req.params.id + ";";
		
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

app.post('/api/poll', function(req, res) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var queryString = "INSERT INTO poll (creator_id, question) VALUES ('"
		+ req.body.creator_id + "', '" + req.body.question + "');";

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


app.listen(app.get('port'), function() {
	console.log("Node app is running at localhost:" + app.get('port'))
});
