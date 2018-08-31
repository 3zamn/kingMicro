$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/role/list',
        datatype: "json",
        colModel: [
            { label: '角色ID', name: 'roleId', width: 45, key: true },
            { label: '角色名称', name: 'roleName',  width: 75 },
            { label: '所属部门', name: 'deptName', width: 75 },
            { label: '备注', name: 'remark', width: 100 },
            { label: '创建时间', name: 'createTime',  width: 80}
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

//菜单树
var menu_ztree;
var menu_setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "menuId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    },
    check:{
        enable:true,
        nocheckInherit:true
    },
    callback: {
        beforeClick: getCurrentNode,
        onClick : zTreeOnClick
    }
};

function getCurrentNode(treeId, treeNode) {
    curNode = treeNode;
    zTreeOnClick(curNode);
}

//设置功能节点列表列
var colParam = new Array();		
function zTreeOnClick(treeNode){

	var params=treeNode.params;
	if(params !=null && JSON.parse(params).pagekey=="setcol" && JSON.parse(params).pagekey!=null){//判断扩展事件
		var json=JSON.parse(params).pagevule;

		 var data = {"menuId":treeNode.menuId,"pagevule":json}
		 var colJson=JSON.stringify(data);
		 colParam.push(data);
	//	 debugger
		$("#colContent").empty();
		var colContent="";	
		for (var i = 0, l = json.length; i < l; i++) {
			var col='<span style="float: left; width: 50%;margin-bottom: 5px;"> <input  type="checkbox" value="'+json[i].name+ '"';
			if(json[i].selected==true){
				col=col+' checked="'+json[i].selected+'">'+ json[i].title+'</span>';
			}else{
				col=col+' >'+ json[i].title+'</span>';
			}    	
	    	 $("#colContent").append(col)
		}

		 layer.open({
	         type: 1,
	         offset: '50px',
	         skin: 'layui-layer-molv',
	         title: "自定义展示列",
	         area: ['460px', '330px'],
	         shade: 0,
	         shadeClose: false,
	         content: jQuery("#colsInfo"),
	         btn: ['确定', '取消'],
	         btn1: function (index) {
	        		var data ="" ;

	             layer.close(index);
	         }
	     });
	}	
}

//授权用户树
var user_ztree;


//部门结构树
var dept_ztree;
var dept_setting = {
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


var dept_select_user = {
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
	    },
	    callback: {
	        beforeClick: getCurrentNode_dept,
	        onClick : zTreeOnClick_dept
	    }
	};

function getCurrentNode_dept(treeId, treeNode) {
    curNode = treeNode;
    zTreeOnClick_dept(curNode);
}

function zTreeOnClick_dept(curNode) {
	 var page = $("#jqGrid_user").jqGrid('getGridParam','page');
	// debugger
     $("#jqGrid_user").jqGrid('setGridParam',{
         postData:{'deptIds': curNode.deptId},
         page:page
     }).trigger("reloadGrid");
}

//数据树
var data_ztree;
var data_setting = {
    data: {
    	 keep: {
             parent: true,
             leaf: true
         },
    	simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    },
    check:{
        enable:true,
        nocheckInherit:true,
        chkboxType:{ "Y" : "", "N" : "" }
    }
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            roleName: null
        },
        showList: true,
        title:null,
        role:{
            deptId:null,
            userIdList:[],
            paramExt:null,
            deptName:null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.role = {deptName:null, deptId:null};
            vm.getMenuTree(null);

            vm.getDept();

            vm.getDataTree();
            vm.getUser(null);
        },
        update: function () {
            var roleId = getSelectedRow();
            var rowData = $("#jqGrid").jqGrid('getRowData',roleId);          
            if(roleId == null){
                return ;
            }
            //扩展参数--动态列设置
            vm.role.paramExt=rowData.paramExt;
            vm.role.userIdList = rowData.userIdList;
            vm.showList = false;
            vm.title = "修改";
            vm.getDataTree();
            vm.getMenuTree(roleId);
            vm.getUser(roleId);
            vm.getDept();
        },
        del: function () {
            var roleIds = getSelectedRows();
            if(roleIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/role/delete",
                    contentType: "application/json",
                    data: JSON.stringify(roleIds),
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
        },
        getRole: function(roleId){
            $.get(baseURL + "sys/role/info/"+roleId, function(r){
                vm.role = r.data;

                //勾选角色所拥有的菜单
                var menuIds = vm.role.menuIdList;
                for(var i=0; i<menuIds.length; i++) {
                    var node = menu_ztree.getNodeByParam("menuId", menuIds[i]);
                    menu_ztree.checkNode(node, true, false);
                }

                //勾选角色所拥有的部门数据权限
                var deptIds = vm.role.deptIdList;
                for(var i=0; i<deptIds.length; i++) {
                    var node = data_ztree.getNodeByParam("deptId", deptIds[i]);
                    data_ztree.checkNode(node, true, false);
                }

                vm.getDept();
            });
        },
        saveOrUpdate: function () {
            //获取选择的菜单
            var nodes = menu_ztree.getCheckedNodes(true);
            var menuIdList = new Array();
            for(var i=0; i<nodes.length; i++) {
                menuIdList.push(nodes[i].menuId);
            }
            vm.role.menuIdList = menuIdList;

            //获取选择的数据
            var nodes = data_ztree.getCheckedNodes(true);
            var deptIdList = new Array();
            for(var i=0; i<nodes.length; i++) {
                deptIdList.push(nodes[i].deptId);
            }
            //获取节点动态列
            var colList = new Array();
            for(var i=0; i<nodes.length; i++) {
            	if(nodes[i].params!=null && JSON.parse(nodes[i].params).pagekey=="setcol"){
            		colList.push(nodes[i].params);
            	}          	
            }
            vm.role.deptIdList = deptIdList;
            vm.role.paramExt=colParam;
            var url = vm.role.roleId == null ? "sys/role/save" : "sys/role/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.role),
                success: function(r){
                    if(r.code === 200){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        getMenuTree: function(roleId) {
            //加载菜单树
            $.get(baseURL + "sys/menu/list", function(r){
                menu_ztree = $.fn.zTree.init($("#menuTree"), menu_setting, r);
                //展开所有节点
                menu_ztree.expandAll(true);

                if(roleId != null){
                    vm.getRole(roleId);
                }
            });
        },
        getDataTree: function(roleId) {
            //加载菜单树
            $.get(baseURL + "sys/dept/list", function(r){
                data_ztree = $.fn.zTree.init($("#dataTree"), data_setting, r);
                //展开所有节点
                data_ztree.expandAll(true);
            });
        },
        getDept: function(){
            //加载部门树
            $.get(baseURL + "sys/dept/list", function(r){
                dept_ztree = $.fn.zTree.init($("#deptTree"), dept_setting, r);
                //加载授权用户树
                user_ztree =$.fn.zTree.init($("#userTree"), dept_select_user, r);
                user_ztree.expandAll(true);
                var node = dept_ztree.getNodeByParam("deptId", vm.role.deptId);
                if(node != null){
                    dept_ztree.selectNode(node);

                    vm.role.deptName = node.name;
                }
            })
        },
        getUser:function(roleId){
        	 $("#jqGrid_user").jqGrid({
        	        url: baseURL + 'sys/user/list/'+roleId,
        	        datatype: "json",
        	        colModel: [			
        				{ label: '用户ID', name: 'userId',  width: 45, key: true ,hidden:true},
        				{ label: '用户名', name: 'username', width: 55 },
        	            { label: '所属部门', name: 'deptName', width: 55 },	
        				{ label: '手机号', name: 'mobile', width: 55 }
        				
        	        ],
        	        viewrecords: true,
        	        height: 250,
        	        rowNum: 10,
        	        rowList : [10,30,50],
        	        rownumbers: false,
        	        rownumWidth: 5, 	     
        	        autowidth:true,
        	        multiselect: true,
        	        pager: "#jqGridPager_user",
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
        	            $("#jqGrid_user").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        	        }
        	    });
        },
        userTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "授权用户",
                area: ['880px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#userLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                //	 debugger
                	 $("#selected").empty();
                	 var col="";
                	 var userIds = $("#jqGrid_user").jqGrid('getGridParam','selarrrow');
                	 for(var i=0;i<userIds.length;i++){
                		 var rowData = $("#jqGrid_user").jqGrid('getRowData',userIds[i]);           		 
                		  col +='<span style="float: left;margin-left: 5px;">'+rowData.username +'&nbsp;' +'</span>';           			       	    	
                	 }
                	 $("#selected").append(col)

                	 vm.role.userIdList=userIds;
                    layer.close(index);
                }
            });
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = dept_ztree.getSelectedNodes();
                    //选择上级部门
                    vm.role.deptId = node[0].deptId;
                    vm.role.deptName = node[0].name;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'roleName': vm.q.roleName},
                page:page
            }).trigger("reloadGrid");
        },
        query_user: function () {
            vm.reload_user();
        },
        reload_user: function () {
        	var keyParam = new Array();		
			keyParam.push('username');
			keyParam.push('mobile');
			var jsonString = JSON.stringify(keyParam);
            vm.showList = true;
            var page = $("#jqGrid_user").jqGrid('getGridParam','page');
            $("#jqGrid_user").jqGrid('setGridParam',{
                postData:{'searchKey': vm.q.key,'keyParam':jsonString},
                page:page
            }).trigger("reloadGrid");
        },
    }
});