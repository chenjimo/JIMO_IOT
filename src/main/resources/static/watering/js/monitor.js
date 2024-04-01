var taskSizeChart = echarts.init(document.getElementById('taskSize'));
var startTime = new Date();

var todaySyncedNum = 0;
var todayNoSyncNum = 0;
var delayNum = 0;

$(function () {
    $('input').bind('input propertychange', function () {
        $('.commonTable tbody tr').hide()
            .filter(":contains('" + ($(this).val()) + "')").show();
    });

    $("#refreshBtn").click(function () {
        initTable();
    });
    initTable();
    setInterval(initTable, 1000 * 10);
    setInterval(tick, 1000);
});

function tick(){
    var today = new Date();
    document.getElementById("localtime").innerHTML = showLocale(today);

    var t = today - startTime;
    var day = Math.floor(t/1000/60/60/24);
    var hour = Math.floor(t/1000/60/60%24);
    var min = Math.floor(t/1000/60%60);
    var sec = Math.floor(t/1000%60);
    $("#runTimeTj").html(day+" 天 "+hour+" 小时 "+min+" 分 "+sec+" 秒");
}

function syncTj() {
    $("#addTj").html(Math.ceil(Math.random() * 10) + " ms");
    $("#updateTj").html(Math.ceil(Math.random() * 10) + " ms");
    $("#deleteTj").html(Math.ceil(Math.random() * 10) + " ms");

    var today = new Date();

    var option = taskSizeChart._option;
    var data0 = option.series[0].data;//本次

    data0.shift();//删除第一个
    data0.push(Math.ceil(Math.random() * 10000) * 2 + 503);//追加一个新数据

    option.xAxis[0].data.shift();
    option.xAxis[0].data.push( today.getMinutes() + ":" + today.getSeconds());//更新x轴

    taskSizeChart.setOption(option);
}

function taskSizeTj(){
    var names = [];
    var values = [];
    var option = {
        color: ['#00b3ac'],
        legend: {
            data: ['同步量统计'],
            x: 'center',
            y: 30
        },
        tooltip : {
            trigger: 'axis',
            axisPointer : {
                type : 'shadow'
            }
        },
        xAxis : [
            {
                data : names,
                type: 'category',
                splitLine:{
                    show: false
                },
                axisLabel: {
                    interval: 0,
                    show: true,
                    textStyle: {
                        color: '#c3dbff',  //更改坐标轴文字颜色
                        fontSize : 14      //更改坐标轴文字大小
                    }
                }
            }
        ],
        yAxis : [
            {
                type : 'value',
                axisLabel: {
                    show: true,
                    textStyle: {
                        color: '#c3dbff'
                    }
                }
            }
        ],
        series : [
            {
                name:'数据量（条）',
                type:'line',
                smooth: true,
                barWidth: '60%',
                data:values
            }
        ]
    };
    var _time = new Date().getTime();
    for(var i = 12; i > 0; i--){
        var _tempDate = new Date(_time - 1000 * 10 * i);
        names.push(_tempDate.getMinutes() + ":" + _tempDate.getSeconds());
        values.push(Math.ceil(Math.random() * 10000) * 2 + 503);
    }
    option.xAxis[0].data.value = names;
    option.series[0].data.value = values;
    taskSizeChart.setOption(option);
}

function timestampToTime(fmt, timestamp) {
    var date = new Date(timestamp);
    var ret;
    var opt = {
        "Y+": date.getFullYear().toString(),        // 年
        "m+": (date.getMonth() + 1).toString(),     // 月
        "d+": date.getDate().toString(),            // 日
        "H+": date.getHours().toString(),           // 时
        "M+": date.getMinutes().toString(),         // 分
        "S+": date.getSeconds().toString()          // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
    };
    for (var k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
        };
    };
    return fmt;
}

function mySort(data){
    var arr = [];
    for(i in data){
        if("flag" != i && "exceptions" != i && "delay" != i){
            var temp = data[i];
            temp["name"] = i;
            arr.push(temp);
        }
    }
    arr.sort(function(a,b){return b['todayOffset'] > a['todayOffset'] ? 1 : -1 })
    return arr;
}

function initTable() {
    taskSizeTj();
    syncTj();
    var HTML = "<thead>\n" +
        "        <td title=\"序号\">序号</td>\n" +
        "        <td title=\"城市\">城市</td>\n" +
        "        <td title=\"总生产量\">总生产量</td>\n" +
        "        <td title=\"总消费量\">总消费量</td>\n" +
        "        <td title=\"今日消费\">今日消费</td>\n" +
        "        <td title=\"今日消费异常\">今日消费异常</td>\n" +
        "        <td title=\"待消费\">待消费</td>\n" +
        "        <td title=\"最近一条数据同步时间\">最近数据同步时间</td>\n" +
        "        <td title=\"状态\">状态</td>\n" +
        "        <td title=\"最新监控刷新时间\">刷新时间</td>\n" +
        "        </thead>\n" +
        "        <tbody>\n";
    var data = getData();
    var myData = mySort(data);
    $(myData).each(function (index, ele) {
        HTML += "<tr>\n" +
            "            <td>" + (index + 1) + "</td>\n" +
            "            <td>" + ele['name'] + "</td>\n" +
            "            <td>" + ele['total'] + "</td>\n" +
            "            <td>" + ele['offset'] + "</td>\n" +
            "            <td><span style='color: #00cc00'>" + ele['todayOffset'] + "</span></td>\n" +
            "            <td>" + ele['todayConsumeError'] + "</td>\n" +
            "            <td>" + ele['overstock'] + "</td>\n" +
            "            <td>" + timestampToTime("YYYY-mm-dd HH:MM:SS", ele['syncDate']) + "</td>\n" +
            "            <td><span class=\"btn btn-small\" style=\"width: 80px\" >正常</span></td>\n" +
            "            <td><span style='color: #00cc00;'>" + timestampToTime("YYYY-mm-dd HH:MM:SS", ele['date']) + "</span></td>\n";
    });
    HTML += "</tbody>";
    $('.commonTable').html(HTML);

    var todaySynced = Math.ceil(Math.random() * 10000) + 500 + todaySyncedNum;
    var todayNoSync = Math.ceil(Math.random() * 30) + 16;
    var delay = parseFloat(Math.ceil(Math.random() * 3)).toFixed(1);

    $("#todaySyncSpan").numberRockInt({
        initNumber: todaySyncedNum,
        lastNumber: todaySynced,
        duration: 5000,
        step: 8
    });

    $("#todayNoSyncSpan").numberRockInt({
        initNumber: todayNoSyncNum,
        lastNumber: todayNoSync,
        step: 5
    });

    $("#syncDelaySpan").numberRockFloat({
        initNumber: delayNum,
        lastNumber: delay,
        fixedSize: 1,
        easing: "linear"
    });

    todaySyncedNum = todaySynced;
    todayNoSyncNum = todayNoSync;
    delayNum = delay;

    $("#syncStateSpan").html("<font color='#00cc00'>正常</font>");

    $('.commonTable tbody tr').hide()
        .filter(":contains('" + ($("#searchText").val()) + "')").show();

}

function showLocale(objD){
    var str,colorhead,colorfoot;
    var yy = objD.getYear();
    if(yy<1900) yy = yy+1900;
    var MM = objD.getMonth()+1;
    if(MM<10) MM = '0' + MM;
    var dd = objD.getDate();
    if(dd<10) dd = '0' + dd;
    var hh = objD.getHours();
    if(hh<10) hh = '0' + hh;
    var mm = objD.getMinutes();
    if(mm<10) mm = '0' + mm;
    var ss = objD.getSeconds();
    if(ss<10) ss = '0' + ss;
    var ww = objD.getDay();
    if  ( ww==0 )  colorhead="<font color=\"#ffffff\">";
    if  ( ww > 0 && ww < 6 )  colorhead="<font color=\"#ffffff\">";
    if  ( ww==6 )  colorhead="<font color=\"#ffffff\">";
    if  (ww==0)  ww="星期日";
    if  (ww==1)  ww="星期一";
    if  (ww==2)  ww="星期二";
    if  (ww==3)  ww="星期三";
    if  (ww==4)  ww="星期四";
    if  (ww==5)  ww="星期五";
    if  (ww==6)  ww="星期六";
    colorfoot="</font>"
    str = colorhead + yy + "-" + MM + "-" + dd + " " + hh + ":" + mm + ":" + ss + "  " + ww + colorfoot;
    return(str);
}

function hideBugBtn() {
    $("#bugBtn").hide();
}

function getData() {
    var _temp = Math.ceil(Math.random() * 290000);
    return {
        "flag": true,
        "北京": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175795148,
            "date": 1608186111190,
            "startDate": 1608175795148,
            "messge": null
        },
        "郑州": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175807331,
            "date": 1608186123476,
            "startDate": 1608175807331,
            "messge": null
        },
        "武汉": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175805062,
            "date": 1608186121035,
            "startDate": 1608175805062,
            "messge": null
        },
        "乌鲁木齐": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": 0,
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175805946,
            "date": 1608186121104,
            "startDate": 1608175805946,
            "messge": null
        },
        "哈尔滨": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175797293,
            "date": 1608186111590,
            "startDate": 1608175797293,
            "messge": null
        },
        "广州": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175796386,
            "date": 1608186112653,
            "startDate": 1608175796386,
            "messge": null
        },
        "济南": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175798776,
            "date": 1608186113376,
            "startDate": 1608175798776,
            "messge": null
        },
        "南宁": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175801541,
            "date": 1608186117416,
            "startDate": 1608175801541,
            "messge": null
        },
        "西安": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175806458,
            "date": 1608186120973,
            "startDate": 1608175806458,
            "messge": null
        },
        "呼和浩特": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175797815,
            "date": 1608186111654,
            "startDate": 1608175797815,
            "messge": null
        },
        "太原": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175804551,
            "date": 1608186120987,
            "startDate": 1608175804551,
            "messge": null
        },
        "沈阳": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175803840,
            "date": 1608186119489,
            "startDate": 1608175803840,
            "messge": null
        },
        "南昌": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175800856,
            "date": 1608186118199,
            "startDate": 1608175800856,
            "messge": null
        },
        "上海": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175802935,
            "date": 1608186117231,
            "startDate": 1608175802935,
            "messge": null
        },
        "成都": {
            "status": true,
            "total": Math.ceil(Math.random() * 9500000),
            "offset": Math.ceil(Math.random() * 940000),
            "overstock": Math.ceil(Math.random() * 10),
            "todayOffset": Math.ceil(Math.random() * 500),
            "todayConsumeError": 0,
            "syncDate": 1608175795859,
            "date": 1608186111374,
            "startDate": 1608175795859,
            "messge": null
        },
        "delay": 0
    };
}
