var RollingSpider = require("rolling-spider");
var drone = new RollingSpider();

// realiza a conexão com o drone

drone.connect(function(){
  drone.setup(function(){
    drone.startPing();
  });
});
