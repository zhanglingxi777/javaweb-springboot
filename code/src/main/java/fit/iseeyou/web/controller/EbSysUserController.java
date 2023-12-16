package fit.iseeyou.web.controller;

import fit.iseeyou.common.domain.AjaxResult;
import fit.iseeyou.web.domain.EbSysUserDomain;
import fit.iseeyou.web.service.IEbSysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/user")
public class EbSysUserController {
    @Autowired
    private IEbSysUserService ebSysUserService;

    @GetMapping
    public AjaxResult list(EbSysUserDomain user) {
        List<EbSysUserDomain> list = ebSysUserService.getList(user);
        return AjaxResult.success(list);
    }
}
