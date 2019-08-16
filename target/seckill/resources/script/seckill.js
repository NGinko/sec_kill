//存放主要交互逻辑代码
//javascript 模块化
var seckill ={
    //封装秒杀相关url
    URL:{
        //请求服务器时间
        now:function(){
            return '/seckill/time/now';
        },
        exposer :function(seckillId){
            return '/seckill/'+seckillId+'/exposer';
        },
        execution: function(seckillId,md5){
            return '/seckill/'+seckillId+'/'+md5+'/execution'
        }

    },
    validatePhone: function(phone){
        if(phone && phone.length==11 && !isNaN(phone)){
            return true;
        }else{
            return false;
        }
    },
    handleSeckillkill: function(seckillId,node){
        //处理秒杀逻辑
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            //回调函数，执行交互流程
            if(result &&result.success){
                var exposer = result.data;
                //控制暴漏秒杀
                if(exposer.exposed){
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId,md5);
                    console.log("killURl"+ killUrl);
                    //绑定一次单击时间
                    $('#killBtn').one('click',function(){
                        //执行秒杀请求
                        //1.先禁用按钮
                        $(this).addClass('disable');
                        //2.发送秒杀请求执行秒杀
                        $.post(killUrl,{},function (result) {
                            if(result && result.success){
                                var killResult = result.data;
                                var state = killResult.state;
                                var stateInfo = killResult.stateInfo;
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }
                        });
                    });
                    node.show();
                }else{
                    //未开启秒杀(浏览器时间偏差
                    var now = expoder['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    console.log(seckillId);
                    seckill.countDown(seckillId, now, start, end);
                }



            }else{
                console.log('result:' + result);
            }
        });
    },
    countDown: function(seckillId,nowTime,startTime,endTime){
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
            console.log('秒杀结束');
        }else if (nowTime < startTime){
            //秒杀未开始，倒计时
            var killTime = new Date(startTime);
            seckillBox.countdown(killTime,function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                console.log('秒杀');
            }).on('finish.countdown',function () {
                //获取秒杀地址 控制显示逻辑 执行秒杀
                seckill.handleSeckillkill(seckillId,seckillBox);
            });
        }else{
            seckill.handleSeckillkill(seckillId,seckillBox);


        }
    }
    ,
    detail:{
        //详情页初始化
        init:function(params) {
            //手机号验证登录，记时交互
            //规划交互流程
            //在cookie中查找手机号
            var killPhone = $.cookie('killPhone');



            if (!seckill.validatePhone(killPhone)) {
                //绑定phone
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({

                    show: true, //显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    console.log(inputPhone);
                    if (seckill.validatePhone(inputPhone)) {

                        //电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }

            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //已经登录，ajax计时交互
            $.get(seckill.URL.now(),{},function(result){

                if(result && result.success){
                    var nowTime = result.data;
                    console.log(startTime);
                    //时间判断计时交互
                    seckill.countDown(seckillId,nowTime,startTime,endTime);

                }else{
                    console.log('result'+reslut.success);
                    console.log('result'+result );
                }
            })
        }


    }
}