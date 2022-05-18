function searchByPara() {
    var account = $("#account").val();
    window.location.href = "/wgcloud/log/list?account=" + escape(escape(account));
}

function add() {
    window.location.href = "/wgcloud/cmdInfo/edit";
}


function view(id) {
    window.location.href = "/wgcloud/cmdInfo/view?id=" + id;
}

function edit(id) {
    window.location.href = "/wgcloud/cmdInfo/edit?id=" + id;
}

function del(id) {
    if (confirm('你确定要删除吗？')) {
        window.location.href = "/wgcloud/cmdInfo/del?id=" + id;
    }
}

function viewDate(id, dates) {
    window.location.href = "/wgcloud/cmdInfo/view?id=" + id + "&date=" + dates;
}

function cancel() {
    history.back();
}
