var amqp = require('amqp');
var fs = require('fs');
var lzutf8 = require('lzutf8'); 
var connection = amqp.createConnection({ host: '130.245.169.67', login:'rabbitmqadmin', password:'rabbitmqadmin'});
// add this for better debuging 
connection.on('error', function(e) {
  console.log("Error from amqp: ",e);
});
 
// Wait for connection to become established. 
connection.on('ready', function () {
  // Use the default 'amq.topic' exchange 
  connection.queue('test2-q',{durable : "true", autoDelete: false}, function (q) {
      // Catch all messages 
      //q.bind('test2-q','dnscap-ex','');
    
      // Receive messages 
      q.subscribe(function (message) {
        // Print messages to stdout 
	var output = lzutf8.decompress(message.data.toString('Base64'),{inputEncoding:"Base64"})
	
	//console.log(JSON.parse(Object.keys(JSON.parse(output)[1])[1]).host) 

	output = (JSON.parse(Object.keys(JSON.parse(output)[1])[1]).host)

	fs.appendFile('/home/ubuntu/dnsMonitor/target/output.txt', output + "\n", function (err) {
        if (err) throw err;
        //console.log('Saved!');
	});




      });
  });
});

