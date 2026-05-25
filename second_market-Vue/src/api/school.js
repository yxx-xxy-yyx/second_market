import request from './request'

export const schoolApi = {
    getSchoolList(params) {
        return request({
            url: '/school/list',
            method: 'get',
            params
        })
    },
    getList(params) {
        return this.getSchoolList(params)
    }
}