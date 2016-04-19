var ok_no = 0;
var nok_no = 0;
var times = 0;
var warnNo = 0;
var efenceNo = 0;
var detect_ms = 5000; // 检测间隔时间
var interval_handle;
var beginInvokeTime = 0;

// common function
function invoke_api(q) {
    var now = new Date();
    beginInvokeTime = now.getHours() * 60 * 60 * 1000 + now.getMinutes() * 60 * 1000 + now.getSeconds() * 1000 + now.getMilliseconds();
    $.ajax({
        url : 'api',
        type : 'post',
        async : false,
        // dataType:'json',
        timeout : 100000,
        error : function(ret) {
            deal_error(ret);
        },
        success : function(xml) {
            deal_result(xml);
        },
        data : q
    });
}
function deal_result(res) {
    if (res.resultCode == 0) {
        set_output(JSON.stringify(res));
        echoOk();
        calcReturn(res);
    } else {
        var out = JSON.stringify(res);
        echoError();
        set_output(out);
        if (res.cmd) {
            $("#console").val($("#console").val() + res.cmd + ":\t" + res.resultNote + "\n");
        } else {
            $("#console").val($("#console").val() + "remote invoke:\t" + res.resultNote + "\n");
        }
    }
    var now = new Date();
    finishInvokeTime = now.getHours() * 60 * 60 * 1000 + now.getMinutes() * 60 * 1000 + now.getSeconds() * 1000 + now.getMilliseconds();
    $("#console").val("[" + res.cmd + "] spend " + (finishInvokeTime - beginInvokeTime) + " ms \n" + $("#console").val());
}

function stopTest() {
    times = 0;
    clearInterval(interval_handle);
}

function calcReturn(res) {
    $("#resultNo").html("<" + res.totalRecordNum + ">");
}

function deal_error(res) {
    set_output(JSON.stringify(res));
    echoError();
}

function echoError() {
    nok_no = nok_no + 1;
    $("#detailResult").html("<font color='green'>成功:[" + ok_no + "]</font>/<font color='red'>失败:[" + nok_no + "]</font>");
}

function echoOk() {
    ok_no = ok_no + 1;
    $("#detailResult").html("<font color='green'>成功:[" + ok_no + "]</font>/<font color='red'>失败:[" + nok_no + "]</font>");
}

function execute(request) {
    if (request) {
        invoke_api(request);
    } else if ($("#testTime").val()) {
        var now = new Date();
        bTime = now.getHours() * 60 * 60 * 1000 + now.getMinutes() * 60 * 1000 + now.getSeconds() * 1000 + now.getMilliseconds();
        for (var i = 0; i < $("#testTime").val(); i++) {
            invoke_api($("#request").val());
        }
        var now = new Date();
        eTime = now.getHours() * 60 * 60 * 1000 + now.getMinutes() * 60 * 1000 + now.getSeconds() * 1000 + now.getMilliseconds();
        $('#msgLabel')
                .html($("#testTime").val() + " invoke spend " + (eTime - bTime) + " ms" + '<input type="button" onclick="format_input()" value="格式化代码" >');
    } else {
        invoke_api($("#request").val());
    }
}

function formatJson(jsonStr) {
    var str = jsonStr.replace(/,/g, ",\n").replace(/:\{/g, ":\n{").replace(/\{\"/g, "{\n\"").replace(/\[\{/g, "[\n{");
    str = str.replace(/\}/g, "\n\}").replace(/\]/g, "\n\]");
    return str;
}

function format_input() {
    var jsontxt = js_beautify(unpacker_filter($("#request").val()), {
        indent_size : 2,
        indent_char : ' ',
        preserve_newlines : true,
        brace_style : 'expand',
        keep_array_indentation : true,
        space_after_anon_function : true
    });
    $("#request").val(jsontxt);
}

function ui_init(request) {
    var jsontxt = js_beautify(unpacker_filter(request), {
        indent_size : 2,
        indent_char : ' ',
        preserve_newlines : true,
        brace_style : 'expand',
        keep_array_indentation : true,
        space_after_anon_function : true
    });
    $("#request").val(jsontxt);
    set_output("");
}

function set_output(response) {
    if (!response) {
        $("#response").val("");
    } else {
        var jsontxt = js_beautify(unpacker_filter(response), {
            indent_size : 2,
            indent_char : ' ',
            preserve_newlines : true,
            brace_style : 'expand',
            keep_array_indentation : false,
            space_after_anon_function : true
        });
        $("#response").val(jsontxt);
    }
}

// business function
function cfgCacheRefresh() {
    var q = '{"cmd":"cfgCacheRefresh", "ver":1}';
    ui_init(q);
}

function addVehicle() {
    var q = '{"cmd":"addVehicle", "ver":1, "brandId":"0010", "brandName":"大众", "licensePlateNo":"苏A10010"}';
    ui_init(q);
}

function allCommand() {
    var q = '{"cmd":"AllCommand", "ver":1}';
    ui_init(q);
}

// ----------------------ecode 数据验证接口-----------------------
function showRequest() {
    eval($("#cmd").val());
    continuousRun();
}

function continuousRun() {
    if ($("#chooseBox").attr("checked")) {
        execute();
    }
}

function dateString() {
    var randSuffix = Math.round(Math.random() * 1000000);
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth();
    var dat = date.getDate();
    var hour = date.getHours();
    var minute = date.getMinutes();
    var second = date.getSeconds();
    var milisecond = date.getMilliseconds();
    return year + "" + month + "" + dat + "" + hour + "" + minute + "" + second;// +""+milisecond+"_"+randSuffix;
}

function autoTest() {
    $("#chooseBox").attr("checked", true);
    times = prompt("请输入测试次数", "1");
    for (var i = 0; i < times; i++) {
        var rand = Math.round(Math.random() * 10000);
        addVehicle();
        continuousRun();
        $("#msgLabel").html((i + 1) + "/" + times + "轮" + '<input type="button" onclick="format_input()" value="格式化代码" >');
        $("#detailResult").html("<font color='#005800'>成功:[" + ok_no + "]</font><font color='red'>失败:[" + nok_no + "]</font>");
    }
    $("#chooseBox").attr("checked", false);
}

function randCategory(randD) {
    var cates = [ 11, 12, 21, 31, 32, 33, 34, 41, 42, 43, 51, 61 ];
    if (randD % 12 == 11) {
        return "";
    } else {
        return cates[randD % 12];
    }
}

function displayCommand() {
    var selvar = document.getElementById("cmd");
    var reqArr = selvar.value.split(",");
    var q = '{"cmd":"CmdReqFormat", "ver":1 , "commandName":"' + reqArr[0] + '" , "commandVer":' + reqArr[1] + '}';
    $.ajax({
        url : 'api',
        type : 'post',
        async : false,
        timeout : 100000,
        error : function(ret) {
            deal_error(ret);
        },
        success : function(resp) {
            ui_init(resp.commandNames);
            set_output(resp.respFormat);
        },
        data : q
    });
}

$(document).ready(showRequest());