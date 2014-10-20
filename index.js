var express = require('express');
var app = express();
var cool = require('cool-ascii-faces');
var pg = require('pg');

app.set('port', (process.env.PORT || 5000));
app.use(express.static(__dirname + '/public'))

app.get('/', function(request, response) {
  response.send('Hello World!')
});

app.get('/poll', function(request, response) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		client.query('SELECT * FROM poll;', function(err, result) {
			done();
			if (err) {
				console.error(err);
				response.send("Error " + error);
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
				response.send("Error " + error);
			} else {
				response.send(result.rows);
			}
		});
	});
});

app.post('/poll', function(req, response) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		client.query("INSERT INTO poll (req.body.creator_id, req.body.question) VALUES ('jk', 'What is a test?');", function(err, result) {
			done();
			if (err) {
				console.error(err);
				response.send("Error " + error);
			} else {
				response.send(result.rows);
			}
		});
	});
});

app.get('/test', function(request, response) {
  var result = '';
  var times = process.env.TIMES || 5;
  for (i=0; i < times; i++) {
  	result += (cool() + "<br>");
  }
  response.send(result);
});

app.get('/db', function(request, response) {
	pg.connect(process.env.DATABASE_URL, function(err, client, done) {
		client.query('SELECT * FROM test_table', function(err, result) {
			done();
			if (err) {
				console.error(err);
				response.send("Error " + error);
			} else {
				response.send(result.rows);
			}
		});
	});
});

app.listen(app.get('port'), function() {
  console.log("Node app is running at localhost:" + app.get('port'))
});
