<template>
    <div class="app-container">
        <!--  导入导出按钮  -->
        <div class="el-toolbar">
            <div class="el-toolbar-body" style="justify-content: flex-start;">
                <!-- <el-button type="text" @click="exportData"> <i class="fa fa-plus"/> 导出</el-button> -->
                <a href="http://localhost:8202/admin/cmn/dict/exportData" target="_blank">
                    <el-button type="text"> <i class="fa fa-plus"/> 导出</el-button>
                </a>

                <el-button type="text" @click="importData"><i class="fa fa-plus"/> 导入</el-button>

            </div>
        </div>

        <!-- 数据字典列表 -->
        <el-table
        :data="list"
        style="width: 100%"
        row-key="id"
        border
        lazy
        :load="getChildrens"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}">

            <el-table-column label="名称" width="230" align="left">
            <template slot-scope="scope">
            <span>{{ scope.row.name }}</span>
            </template>
            </el-table-column>

            <el-table-column label="编码" width="220">
            <template slot-scope="{row}">
                    {{ row.dictCode }}
            </template>
            </el-table-column>
            <el-table-column label="值" width="230" align="left">
            <template slot-scope="scope">
            <span>{{ scope.row.value }}</span>
            </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center">
            <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
            </template>
            </el-table-column>
        </el-table>

        <!-- 导入弹框 -->
        <el-dialog title="导入" :visible.sync="dialogImportVisible" width="480px">
            <el-form label-position="right" label-width="170px">
                <el-form-item label="文件">
                <el-upload
                :multiple="false"
                :on-success="onUploadSuccess"
                :action="'http://localhost:8202/admin/cmn/dict/importData'"
                class="upload-demo">
                <el-button size="small" type="primary">点击上传</el-button>
                <div slot="tip" class="el-upload__tip">只能上传excel文件，且不超过500kb</div>
                </el-upload>
                </el-form-item>
            </el-form>

            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogImportVisible = false">
                    取消
                </el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script>
import dict from '@/api/dict'

export default ({
    data() {
        return {
            list: [], // 数据字典列表数组
            dialogImportVisible: false, // 设置导入弹框是否显示
        }
    },
    created() {
        this.getDictList(1)
    },
    methods: {
        // 数据字典列表 显示全部数据
        getDictList(id) {
            dict.dictList(id)
            .then(response => {
                this.list = response.data
            })
        },
        // 查询下一层级数据
       getChildrens(tree, treeNode, resolve) {
            dict.dictList(tree.id).then(response => {
                resolve(response.data)
            })
        },
        // 导出数据字典
        exportData() {
            window.location.href = 'http://localhost:8202/admin/cmn/dict/exportData'
        },
        // 导入数据字典,使用弹出层
        importData() {
            this.dialogImportVisible = true
        },
        // 上传成功调用
        onUploadSuccess() {
            this.$message.info('上传成功')
            // 关闭弹窗
            this.dialogImportVisible = false
            // 刷新页面
            this.getDictList(1)
        }
    }
})
</script>
