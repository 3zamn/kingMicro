$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/online',
        datatype: "json",
        colModel: [
        	{ label: 'ID', name: 'Id',  width: 45, key: true,hidden:true },
			{ label: '用户ID', name: 'userId',  width: 45 },
			{ label: '用户名', name: 'userName', width: 45 },
            { label: 'token', name: 'token', width: 150 },
            { label: '登录ip', name: 'ip', width: 80 },
        	{ label: '登录时间', name: 'updateTime', width: 80 },
			{ label: '失效时间', name: 'expireTime', width: 80 }
		
			
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
var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            username: null
        },
        showList: true,
        title:null,
        roleList:{},
        position: '',
        dicSelect:{},
        currentUserId:null,
        user:{
        	userId:null,
        	status:1,
            deptId:null,
            deptName:null,
            roleIdList:[]
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
  
        del: function () { 	
        	var rowids = new Array();
        	var tokens = new Array();
        	 rowids = $("#jqGrid").getGridParam("selarrrow");    	
        	//debugger
        	for(var index  in rowids){
        		var rowData = $("#jqGrid").getRowData(rowids[index]);  //1.获取选中行的数据 	
        		tokens.push(rowData.token);
        	}
            if(tokens.length>0){
            	 confirm('确定要注销该用户登录？', function(){
                     $.ajax({
                         type: "POST",
                         url: baseURL + "sys/user/offline",
                         contentType: "application/json",
                         data: JSON.stringify(tokens),
                         success: function(r){
                             if(r.code == 200){
                                 alert('操作成功', function(){
                                     vm.reload();
                                 });
                             }else{
                                 alert(r.msg);
                             }
                         }
                     });
                 });           
            }else{
            	 alert('请选择!')
            	 return ;
            }

           
        },
      
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
         
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{},		
                page:page
            }).trigger("reloadGrid");
        }
    }
});