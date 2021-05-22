import request from '@/utils/request'
const api_name = '/admin/order/orderInfo'
export default {
    // 订单列表
    getPageList (page, limit, searchObj) {
        return request({
            url: `${api_name}/${page}/${limit}`,
            method: 'get',
            params: searchObj
        })
    },
    // 订单状态
    getStatusList () {
        return request({
            url: `${api_name}/getStatusList`,
            method: 'get'
        })
    },
    // 通过id获取订单详情
    getById (id) {
        return request({
            url: `${api_name}/show/${id}`,
            method: 'get'
        })
    }

}
