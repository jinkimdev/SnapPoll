var express = require('express');
var app = express();
var pg = require('pg');
var bodyParser = require('body-parser');
// parse urlencoded request bodies into req.body
app.use(bodyParser.urlencoded({
  extended: true
}));
app.use(bodyParser.json());

app.set('port', (process.env.PORT || 5000));

app.use(express.static(__dirname + '/public'));


/* ######## REST API ######## */

app.get('/', function(request, response) {
  response.send('Hello World!')
});

app.get('/poll', function(request, response) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		client.query('SELECT * FROM poll;', function(err, result) {
			done();
			if (err) {
				console.error(err);
				response.send("Error " + err);
			} else {
				response.send(result.rows);
			}
		});
	});
});

app.get('/poll/:id', function(request, response) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		client.query('SELECT * FROM poll WHERE poll_id='+request.params.id+';', function(err, result) {
			done();
			if (err) {
				console.error(err);
				response.send("Error " + err);
			} else {
				response.send(result.rows);
			}
		});
	});
});

app.post('/poll', function(req, response) {

	// response.send("HERE-- " + req.body);
	// console.log(req.body);

	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		var q = "INSERT INTO poll (creator_id, question) VALUES ('"
		+ req.body.creator_id + "', '" + req.body.question + "');";

		client.query(q, function(err, result) {
			done();
			if (err) {
				console.error(err);
				response.send("Error " + err);
			} else {
				response.send(req.headers + "<br>-------<br>" + result.rows);
			}
		});
	});
});

app.get('/test', function(request, response) {
	response.send("TEST 202");
});

app.get('/db', function(request, response) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		client.query('SELECT * FROM test_table', function(err, result) {
			done();
			if (err) {
				console.error(err);
				response.send("Error " + err);
			} else {
				response.send(result.rows);
			}
		});
	});
});

app.listen(app.get('port'), function() {
  console.log("Node app is running at localhost:" + app.get('port'))
});
