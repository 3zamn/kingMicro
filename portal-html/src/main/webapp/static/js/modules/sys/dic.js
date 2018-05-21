var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
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
        showList: true,
        title: null,
        sysDic:{
            parentName:null,
            parentId:0,
            sortNo:0,
            type:1
        },
        q:{
			key: null
		}
    },
    methods: {
    	query: function () {
			vm.reload();
		},
    	getDirectory: function(){
            //加载菜单树
            $.get(baseURL + "sys/dic/select", function(r){
                ztree = $.fn.zTree.init($("#directoryTree"), setting, r.data);
                var node = ztree.getNodeByParam("id", vm.sysDic.parentId);
                ztree.selectNode(node);
                vm.sysDic.parentName = node.name;
            })
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.sysDic = {parentName:null,parentId:0,enable:1,sortNo:0,type:1};
            vm.getDirectory();
        },
        update: function () {
            var id = getDicId();
            if(id == false){
                return ;
            }

            $.get(baseURL + "sys/dic/info/"+id, function(r){
                vm.showList = false;
                vm.title = "修改";
                vm.sysDic = r.data;

                vm.getDirectory();
            });
        },
        del: function () {
            var id = getDicId();
            if(id == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/dic/delete",
                    data: "id=" + id,
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
            });
        },
        saveOrUpdate: function () {
            if(vm.validator()){
                return ;
            }

            var url = vm.sysDic.id == null ? "sys/dic/save" : "sys/dic/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.sysDic),
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
        directoryTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择字典目录",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#directoryLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级菜单
                    vm.sysDic.parentId = node[0].id;
                    vm.sysDic.parentName = node[0].name;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            Dic.table.refresh();
        },
        validator: function () {
        	//		debugger
        			if(vm.sysDic.type==1){
        				if(isBlank(vm.sysDic.value)){
        					alert("值不能为空");
        					return true;
        				}

        	            if(isBlank(vm.sysDic.text)){
        	                alert("项不能为空");
        	                return true;
        	            }
        	            if(isBlank(vm.sysDic.parentId)){
        	                alert("字典所在目录不能为空");
        	                return true;
        	            }
        			}
        			if(vm.sysDic.type==0){
        				if(isBlank(vm.sysDic.name)){
        					alert("字典名称不能为空");
        					return true;
        				}

        	            if(isBlank(vm.sysDic.code)){
        	                alert("字典编码不能为空");
        	                return true;
        	            }          
        			}
        			
        }
    }
});


var Dic = {
    id: "dicTable",
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Dic.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: 'ID', field: 'id', visible: false, align: 'center', valign: 'middle', width: '20px'},
        {title: '字典名称', field: 'name', align: 'center', valign: 'middle', sortable: true, width: '80px'},
        {title: '字典编码', field: 'code', align: 'center', valign: 'middle', sortable: true, width: '40px'},
        {title: '上级目录', field: 'parentName', align: 'center', valign: 'middle', sortable: true, width: '60px'}, 
        {title: '项', field: 'text', visible: false, align: 'center', valign: 'middle', width: '60px'},
        {title: '值', field: 'value', visible: false, align: 'center', valign: 'middle', width: '40px'},
        {title: '排序', field: 'sortNo', visible: false, align: 'center', valign: 'middle', width: '20px'},
        {title: '类型', field: 'type', align: 'center', valign: 'middle', sortable: true, width: '40px', formatter: function(item, index){
            if(item.type === 0){
                return '<span class="label label-primary">目录</span>';
            }
            if(item.type === 1){
                return '<span class="label label-success">字典项</span>';
            }          
        }},
        {title: '启用状态', field: 'enable', align: 'center', valign: 'middle', sortable: true, width: '40px', formatter: function(item, index){
            if(item.enable === 0){
                return '<span class="label label-primary">禁用</span>';
            }
            if(item.enable === 1){
                return '<span class="label label-success">启用</span>';
            }          
        }},
        {title: '备注', field: 'remark', align: 'center', valign: 'middle', sortable: true, width: '60px'},
      /*  {title: '创建者', field: 'createBy', align: 'center', valign: 'middle', sortable: true, width: '40px'},*/
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true,width: '80px'}]
    return columns;
};


function getDicId () {
    var selected = $('#dicTable').bootstrapTreeTable('getSelections');
    if (selected.length == 0) {
        alert("请选择一条记录");
        return false;
    } else {
        return selected[0].id;
    }
}


$(function () {
    var colunms = Dic.initColumn();
    var table = new TreeTable(Dic.id, baseURL + "sys/dic/list", colunms);
    table.setExpandColumn(2);
    table.setIdField("id");
    table.setCodeField("id");
    table.setParentCodeField("parentId");
    table.setExpandAll(false);
    table.init();
    Dic.table = table;
});
