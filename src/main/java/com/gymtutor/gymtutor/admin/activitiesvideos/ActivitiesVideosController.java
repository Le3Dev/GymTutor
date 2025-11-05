package com.gymtutor.gymtutor.admin.activitiesvideos;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/activities/{activitiesId}/videos")
public class ActivitiesVideosController {

    @Autowired
    private ActivitiesVideosService videosService;

    @GetMapping
    public String listVideos(
            @PathVariable int activitiesId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        return handleRequest(redirectAttributes, model, null, null, activitiesId,()->{
            var videosList = videosService.findByActivityModelActivitiesId(activitiesId);
            model.addAttribute("videosList", videosList);
            model.addAttribute("activitiesId", activitiesId);
            model.addAttribute("body", "/admin/activities/videos/list");
            return "/fragments/layout";
        });
    }

    @GetMapping("/new")
    public String newVideoForm(
            Model model,
            @PathVariable int activitiesId
    ){
        model.addAttribute("activitiesVideosModel", new ActivitiesVideosModel());
        model.addAttribute("activitiesId", activitiesId);
        model.addAttribute("body", "/admin/activities/videos/new");
        return "/fragments/layout";
    }

    @PostMapping("/new")
    public String createVideo(
            @PathVariable int activitiesId,
            @Valid @ModelAttribute ActivitiesVideosModel activitiesVideosModel,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if(bindingResult.hasErrors()){
            return handleValidationErrors(model, "/admin/activities/videos/new", activitiesVideosModel, bindingResult, activitiesId,null);
        }

        return  handleRequest(redirectAttributes, model, "admin/activities/videos/new", activitiesVideosModel, activitiesId, ()-> {
            videosService.createVideo(activitiesVideosModel, activitiesId);
            redirectAttributes.addFlashAttribute("successMessage","Vídeo adicionado com sucesso!!!");
            return "redirect:/admin/activities/"+activitiesId+"/videos";
        });
    }

    @GetMapping("/{videoId}/edit")
    public String editVideoForm(
            @PathVariable int activitiesId,
            @PathVariable int videoId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        return handleRequest(redirectAttributes, model, null, null, activitiesId, () -> {
            ActivitiesVideosModel activitiesVideosModel = videosService.findById(videoId);
            model.addAttribute("activitiesVideosModel", activitiesVideosModel);
            model.addAttribute("videoId", videoId);
            model.addAttribute("body", "/admin/activities/videos/edit");
            return "/fragments/layout";
        });
    }

    @PostMapping("/{videoId}/edit")
    public String updateVideo(
            @PathVariable int activitiesId,
            @PathVariable int videoId,
            @Valid @ModelAttribute ActivitiesVideosModel activitiesVideosModel,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if(bindingResult.hasErrors()){
            return handleValidationErrors(model, "/admin/activities/videos/edit", activitiesVideosModel, bindingResult, activitiesId, videoId);

        }return handleRequest(redirectAttributes, model, "/admin/activities/videos/edit", activitiesVideosModel,activitiesId,()->{
            videosService.updateVideo(activitiesVideosModel, videoId);
            redirectAttributes.addFlashAttribute("successMessage","Video alterado com sucesso!!!");
            return "redirect:/admin/activities/"+activitiesId+"/videos";
        });
    }

    @PostMapping("/delete/{videoId}")
    public String deleteVideo(
            @PathVariable int videoId,
            @PathVariable int activitiesId,
            RedirectAttributes redirectAttributes
    ){
        try {
            videosService.deleteVideo(videoId);
            redirectAttributes.addFlashAttribute("successMessage", "Item deletado com sucesso!");
            return "redirect:/admin/activities/"+activitiesId+"/videos";
        } catch (DataIntegrityViolationException ex){
            redirectAttributes.addFlashAttribute("errorMessage","nao é possível excluir o video pois ha registros dependentes");
            return "redirect:/admin/activities/"+activitiesId+"/videos";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/activities/"+activitiesId+"/videos";
        }
    }

    private String handleValidationErrors(Model model, String view, ActivitiesVideosModel activitiesVideosModel, BindingResult bindingResult, Integer activitiesId, Integer videoId){
        model.addAttribute("errorMessage", "Há erros no formulário!");
        model.addAttribute("org.springframework.validation.BindingResult.activitiesVideosModel", bindingResult);
        model.addAttribute("ActivitiesVideosModel", activitiesVideosModel);
        model.addAttribute("activitiesId", activitiesId);

        if(videoId != null){
            model.addAttribute("videoId", videoId);
        }
        model.addAttribute("body", view);

        return "/fragments/layout";
    }

    private String handleRequest(RedirectAttributes redirectAttributes, Model model, String view, ActivitiesVideosModel activitiesVideosModel, Integer activitiesId, RequestHandler block
    ){
        try{
            return block.execute();
        }catch (Exception ex) {
            return handleException(ex, model, activitiesVideosModel, activitiesId, view, redirectAttributes);
        }
    }

    @FunctionalInterface
    public interface RequestHandler{
        String execute();
    }

    private String handleException(Exception ex, Model model, ActivitiesVideosModel activitiesVideosModel, Integer activitiesId, String view, RedirectAttributes redirectAttributes){
        return switch (ex) {
            case IllegalArgumentException illegalArgumentException ->
                    handleIllegalArgumentException(illegalArgumentException, model, activitiesVideosModel,activitiesId , view);
            case IllegalAccessException illegalAccessException ->
                    handleIllegalAccessException(illegalAccessException, redirectAttributes, activitiesId);
            case DataIntegrityViolationException ignored ->
                    handleDataIntegrityViolationException(model, activitiesVideosModel, activitiesId, view);
            case null, default -> handleUnexpectedException(model, activitiesVideosModel, activitiesId, view);
        };
    }

    private String handleIllegalArgumentException(IllegalArgumentException ex, Model model, ActivitiesVideosModel activitiesVideosModel,Integer activitiesId, String view){
        return handleError(ex.getMessage(), model, activitiesVideosModel, activitiesId, view);
    }

    private String handleIllegalAccessException(IllegalAccessException ex, RedirectAttributes redirectAttributes, Integer activitiesId){
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/admin/activities/"+ activitiesId +"/videos";
    }

    private String handleDataIntegrityViolationException(Model model, ActivitiesVideosModel activitiesVideosModel, Integer activitiesId, String view){
        return handleError("Erro de integridade de dados.", model, activitiesVideosModel, activitiesId, view);
    }

    private String handleUnexpectedException(Model model, ActivitiesVideosModel activitiesVideosModel,Integer activitiesId, String view){
        return handleError("Erro inesperado. Tente novamente.", model, activitiesVideosModel, activitiesId, view);

    }

    private String handleError(String errorMessage, Model model, ActivitiesVideosModel activitiesVideosModel, Integer activitiesId, String view){
        if (model != null) {
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("activitiesVideosModel", activitiesVideosModel);
            model.addAttribute("activitiesId", activitiesId);
        }
        if(view != null){
            assert model != null;
            model.addAttribute("body", view);
            return "/fragments/layout";
        }else{
            return "redirect:/admin/activities/"+activitiesId+"/videos";
        }
    }
}