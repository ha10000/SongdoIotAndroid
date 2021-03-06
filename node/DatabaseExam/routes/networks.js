var express = require('express');
var router = express.Router();

// haha start
var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'root',
  password : 'test1234',
  database : 'iot'
});
connection.connect();
//------------------
var MongoClient = require('mongodb').MongoClient;
 
// Connection URL
var url = 'mongodb://localhost:27017/iot';
var dbObj = null;

// Use connect method to connect to the Server
MongoClient.connect(url, function(err, db) {
  console.log("Connected correctly to server");
  dbObj = db;
});
// haha end
//-----------------
var redis = require('redis');
var redisClient = redis.createClient();



/* GET users listing. */
// 전체 IP 목록 조회 GET - /networks
router.get('/', function(req, res, next) {
  // res.send('respond with a resource');
  connection.query('select srv_ip, srv_port, mongo_ip, mongo_port from network',
  	function(err, results, fields){
  		if(err){
  			res.send(JSON.stringify(err));
  		}else{
  			res.send(JSON.stringify(results));
  		}
  	});
});
//  IP 목록 삽입  PUT - /networks
router.post('/', function(req, res, next) {

	var server_ip = req.body.server_ip;
	var server_port = req.body.server_port;

	var mongo_ip = req.body.mongo_ip;	
	var mongo_port = req.body.mongo_port;
	var mongo_ip = req.body.mongo_ip;
	var mongo_port = req.body.mongo_port;
console.log("hahaha");
//console.log(JSON.stringify(req));
	console.log("server_ip :"+server_ip);
	console.log("server_port :"+server_port);
	console.log("mongo_ip :"+mongo_ip);
	console.log("mongo_port :"+mongo_port);
	

	//res.send(req);

	connection.query(
		'insert into network(srv_ip,srv_port,mongo_ip,mongo_port ) values(?,?,?,?)',
		[ server_ip, server_port, mongo_ip, mongo_port], function(err, result) {
			if (err) {
				res.send(JSON.stringify(err));
			} else {
				res.send(JSON.stringify(result));
			}
		});
});

/*
// 특정 IP 정보 조회 :   GET    - /networks/id
router.get('/:id', function(req, res, next){
	// Redis에 캐시된 데이터 유무확인
	// 복합키= "GET:/users/id:7" (: ->구분자 ) METHOD:URL:VALUE ** important!!
	redisClient.get('GET:/users/id:'+req.params.id, function(err,data){
		if(err) res.send(JSON.stringify(err));
		else{
			if(data != null){ // Redis에 저장된 데이터가 있는 경우
				res.send(data);
			}else{ // Redis에 저장된 데이터가 없을 경우 -> 직접쿼리 실행
				connection.query('select id, email, name, age from user where id=?',
					[req.params.id],function(err, results, fields){
						// console.log(JSON.stringify(fields));
						if (err){
							res.send(JSON.stringify(err));
						} else {
							if( results.length > 0){
								// Application Side Join-----------------
								connection.query(
									'select * from device where user_id=?',
									[req.params.id],
									function(err2, results2, fields2){
										if(err2) res.send(JSON.stringify(err2));
										else{
											var logs = dbObj.collection('logs');
											logs.find({user_id:Number(req.params.id)}).
												toArray(function(err3, results3){
													if(err3)
														res.send(JSON.stringify(err3));
													else{
														results[0].devices = results2;
														results[0].logs = results3;
														redisClient.setex('GET:/users/id:'+req.params.id,300,
															JSON.stringify(results[0]));
														res.send(JSON.stringify(results[0]));
													}
												});
										}
											
									});
									//------------------
							} else {
								res.send(JSON.stringify({}));
							}
							
						}	
					});
			}
		}
	});


});



// 사용자 Networks정보  추가(가입) : POST   - /networks
var crypto = require('crypto');
router.post('/', function(req, res,next){
	var email = req.body.email;
	var password = req.body.password;
	var name = req.body.name;
	var age = req.body.age;
	console.log(email+ ', ' +password+', '+name+', '+age);
	var hash = crypto.createHash("sha512").update(password).digest('base64');
	console.log(hash);
	connection.query(
		'insert into user(email,password,name,age) values(?,?,?,?)',
		[email, hash, name, age ],
		function(err, result){
			if (err){
				res.send(JSON.stringify(err));
			} else {
				res.send(JSON.stringify(result));
			}	
		});
	// res.send(JSON.stringify({email:email, password:hash, name:name, age:age}));
});
*/


// 사용자 정보 수정 :       PUT    - /users/id
router.put('/:id', function(req,res,next){
	var srv_ip = req.body.srv_ip;
	var srv_port = req.body.srv_port;
	var mongo_ip = req.body.mongo_ip;
	var mongo_port = req.body.mongo_port;
	console.log(srv_ip+ ', ' +srv_port+', '+mongo_ip+', '+mongo_port);

	var query = 'update network set ';
	var conditions = [];
	if( srv_ip != undefined){
		query += "srv_ip=?,";	conditions.push(srv_ip);
	}
	if( srv_port != undefined){
		// var hash = crypto.createHash("sha512").update(password).digest('base64');
		query += "srv_port=?,"; conditions.push(srv_port);
	}
	if(mongo_ip != undefined){
		query += "mongo_ip=?,"; 	conditions.push(mongo_ip);
	}
	if(mongo_port != undefined){
		query += "mongo_port=?"; 		conditions.push(mongo_port); 
	}
	if( query[query.length-1] == ',') // 쉼표제거
		query = query.substring(0, query.length-1);
	query += " where id=?";
	console.log(query);
	console.log(conditions);

	conditions.push(req.params.id);
	connection.query(query, conditions,
	// connection.query(
	// 	'update user set email=?,password=?,name=?,age=? where id=?',
	// 	[email, hash, name, age, req.params.id ],
		function(err, result){
			if(err){
				res.send(JSON.stringify(err));
			}else{
				res.send(JSON.stringify(result));
			}
		})
	// res.send(JSON.stringify({id:req.params.id}));

});
// 사용자 정보 삭제 :       DELETE - /users/id
router.delete('/:id', function(req,res,next){
	connection.query('delete from user where id=?',
		[req.params.id], function(err, result){
			if(err){
				res.send(JSON.stringify(err));
			}else{
				res.send(JSON.stringify(result));
			}
		});
	// res.send(JSON.stringify({id:req.params.id}));
});


module.exports = router;
