function add() {
    window.location.href = "/wgcloud/heathMonitor/edit";
}

function wl() {
    window.location.href = "/wgcloud/dash/warnlist?group="+ $("#sk").val();;
}

function wl1() {
    window.location.href = "/wgcloud/heathMonitor/list?heathGroup="+ $("#sk").val();;
}

function view(id) {
    window.location.href = "/wgcloud/heathMonitor/view?id=" + id;
}
function view1(id) {
    window.open("/wgcloud/heathMonitor/view?id=" + id);
}

function test() {
    var PARAMS={};
    PARAMS.search=$('#form1').formGet();
    var temp = document.createElement("formx");
    temp.action = "/wgcloud/heathMonitor/test";
    temp.method = "post";
    temp.target = "_blank";
    temp.style.display = "none";
    for (var x in PARAMS.search) {
        var opt = document.createElement("textarea");
        if(x=="name"||x=="confidence"||x=="hidden"||x=="version"){
            opt.name = x;
            opt.value = PARAMS.search[x];
            temp.appendChild(opt);
        }
    }
    document.body.appendChild(temp);
    temp.submit();
    //window.open("/wgcloud/heathMonitor/view?id=" + id);
}

function del(id) {
    if (confirm('你确定要删除吗？')) {
        window.location.href = "/wgcloud/heathMonitor/del?id=" + id;
    }
}

function edit(id) {
    window.location.href = "/wgcloud/heathMonitor/edit?id=" + id;
}

function cancel() {
    history.back();
}
