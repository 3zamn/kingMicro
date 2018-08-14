$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/log/exception',
        datatype: "json",
        colModel: [
        	{ label: 'id', name: 'id', width: 20, key: true },
			{ label: '异常流水号', name: 'seriaNo', width: 100},
			{ label: '服务', name: 'appCode', width: 30 }, 			
		/*	{ label: '日志编码', name: 'logCode', width: 50 }, 			*/
			{ label: '异常接口', name: 'apiName', width: 80 }, 			
			{ label: '请求参数', name: 'inputData', width: 60 },
		/*	{ label: '返回结果', name: 'outputData', width: 60 },*/
			{ label: '服务IP', name: 'ip', width: 45 }, 			
			{ label: '发生时间', name: 'createTime', width: 70 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "data.list",
            page: "data.currPage",
            total: "data.totalPage",
            records: "data.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			key: null
		},
		showList: true,
		title: null,
		exception: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		view: function (event) {
//			debugger
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "异常详细";
            
            vm.getInfo(id)
		},
		getInfo: function(id){
			$.get(baseURL + "sys/log/exceptionDetail/"+id, function(r){
                vm.exception = r.data;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				   postData:{'seriaNo': vm.q.key},
			/*	  postData:{'key': vm.q.key},*/
                page:page
            }).trigger("reloadGrid");
		}
	}
});