$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/generator/list',
        datatype: "json",
        colModel: [			 	
			{ label: '表名', name: 'tableName', width: 100, key: true },
			{ label: 'Engine', name: 'engine', width: 70},
			{ label: '库名', name: 'dataSource', width: 70 },
			{ label: '表备注', name: 'tableComment', width: 100 },
			{ label: '创建时间', name: 'createTime', width: 100 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50,100,200],
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
			tableName: null
		},
		title: null,
		showList: true,
		columns: {},
		selected: '',
		dataSource:{}
	},
	created: function(){
		this.getDic();
	},
	methods: {		
		getDic: function () {//下拉选项字典查询
			 $.get(baseURL + "sys/dic/query/"+"codeGen", function(r){
			        vm.dataSource = r.data;
			   });
		},
		query: function () {
			vm.reload();
		/*	$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'tableName': vm.q.tableName},
                page:1 
            }).trigger("reloadGrid");*/
		},
		getInfo: function () {
			var id = getSelectedRow();
			var rowid = $("#jqGrid").getGridParam("selrow");
			var rowData = $("#jqGrid").getRowData(rowid);  //1.获取选中行的数据
	        var dataSource = rowData.dataSource
			if(id == null){
				return ;
			}
			
			$.get(baseURL + "sys/generator/info/"+dataSource+"/"+id, function(r){
                vm.showList = false;
            //    debugger
                vm.title = "配置(未完成。。)";
                vm.columns = r.data.columns;
                
            });
		},
		generator: function() {
			var tableNames = getSelectedRows();
			var rowid = $("#jqGrid").getGridParam("selrow");
            var rowData = $("#jqGrid").getRowData(rowid);  //1.获取选中行的数据
            var dataSource = rowData.dataSource
	//		debugger
			if(tableNames == null){
				return ;
			}
			var token = localStorage.getItem("token");
			 var url= baseURL + "sys/generator/code?tables=" + JSON.stringify(tableNames)+"&token="+token+"&dataSource="+dataSource;
			location.href=encodeURI(url)//转码下以免被高版本tomcat过滤特殊字符报错
		},
		reload: function () {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'tableName': vm.q.tableName,'key': vm.q.key,'dataSource':vm.selected},
                page:page
            }).trigger("reloadGrid");
		},
	}
});

