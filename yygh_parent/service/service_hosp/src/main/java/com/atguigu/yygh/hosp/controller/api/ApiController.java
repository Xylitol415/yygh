package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    // 删除排班信息
    @ApiOperation(value = "删除排班信息")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        // 获取传递过来的排班信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 医院编号
        String hoscode = (String) paramMap.get("hoscode");
        // 科室编号
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        // 当前页和每页记录数
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));

        // TODO 签名校验
        // 1.获取医院系统传递过来的签名
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        //String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }
    // 查询排班接口
    @ApiOperation(value = "获取排班信息")
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request) {
        // 获取传递过来的排班信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 医院编号
        String hoscode = (String) paramMap.get("hoscode");
        // 科室编号
        String depcode = (String) paramMap.get("depcode");
        // 当前页和每页记录数
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));

        // TODO 签名校验
        // 1.获取医院系统传递过来的签名
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        //String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 条件+分页查询排班信息
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel = scheduleService.findPageSchedule(page, limit, scheduleQueryVo);
        return Result.ok(pageModel);

    }

    // 上传排班接口
    @ApiOperation(value = "上传排班信息")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        // 获取传递过来的排班信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // TODO 签名校验
        // 1.获取医院系统传递过来的签名
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.save(paramMap);
        return Result.ok();
    }

    // 删除科室接口
    @ApiOperation(value = "删除科室信息")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        // 获取传递过来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        // 医院编号 和 科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        // TODO 签名校验
        // 1.获取医院系统传递过来的签名
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        //String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    // 查询科室接口
    @ApiOperation(value = "获取科室信息")
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request) {
        // 获取传递过来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 医院编号
        String hoscode = (String) paramMap.get("hoscode");
        // 当前页和每页记录数
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));

        // TODO 签名校验
        // 1.获取医院系统传递过来的签名
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        //String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }


    // 上传科室接口
    @ApiOperation(value = "上传科室信息")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        // 获取传递过来的科室信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 1.获取医院系统传递过来的签名
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);

        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        // 调用service的方法
        departmentService.save(paramMap);
        return Result.ok();
    }

    // 查询医院接口
    @ApiOperation(value = "获取医院信息")
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request) {
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 1.获取医院系统传递过来的签名,签名已经进行了MD5加密，参见hospital-manage模块中ApiServiceImpl.java中的实现
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);


        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        // 调用service的方法实现根据医院编号查询
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    // 上传医院接口
    @ApiOperation(value = "上传医院信息")
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request) {
        // 获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        // 1.获取医院系统传递过来的签名,签名已经进行了MD5加密，参见hospital-manage模块中ApiServiceImpl.java中的实现
        String hospSign = (String)paramMap.get("sign");

        // 2.根据传递过来的医院编码，查询数据库，对应编码的医院签名
        String hoscode = (String)paramMap.get("hoscode");

        // 3.调用hospitalSetService返回的查询到的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);


        // 传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String)paramMap.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }


        // 4.比较:将数据库中编号为hoscode的医院的签名signKey加密后的值 和医院系统传递过来的hospSign是否相同
        String signKeyMD5 = MD5.encrypt(signKey);
        if(!signKeyMD5.equals(hospSign)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        // 调用service的方法
        hospitalService.save(paramMap);
        return Result.ok();
    }
}
