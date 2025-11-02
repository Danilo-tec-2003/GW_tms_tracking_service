package com.gwsistemas.tracking.controller;

import com.gwsistemas.tracking.dto.input.OccurrenceCreateDTO;
import com.gwsistemas.tracking.dto.input.OrderCreateDTO;
import com.gwsistemas.tracking.dto.output.OrderDetailsDTO;
import com.gwsistemas.tracking.enums.TrackingStatus;
import com.gwsistemas.tracking.exception.BusinessRuleException;
import com.gwsistemas.tracking.exception.ResourceNotFoundException;
import com.gwsistemas.tracking.service.TrackingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    private final TrackingService trackingService;

    public WebController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    /**
     * Redireciona para a página principal de consulta de encomendas.
     */
    @GetMapping("/")
    public String showHomePage() {
        return "redirect:/consulta";
    }

    /**
     * Exibe a página de consulta de rastreio.
     * Se for passado um código de rastreio, tenta buscar os detalhes da encomenda.
     */
    @GetMapping("/consulta")
    public String getTrackingDetails(
            @RequestParam(name = "trackingCode", required = false) String trackingCode,
            Model model) {

        if (trackingCode != null && !trackingCode.isEmpty()) {
            try {
                OrderDetailsDTO details = trackingService.getTrackingDetails(trackingCode);
                model.addAttribute("timeline", details);
            } catch (ResourceNotFoundException e) {
                model.addAttribute("erroConsulta", e.getMessage());
            }
        }

        return "consulta";
    }

    /**
     * Exibe a página de cadastro de novas ocorrências.
     */
    @GetMapping("/cadastro")
    public String showCadastroPage() {
        return "cadastro";
    }

    /**
     * Registra uma nova ocorrência para uma encomenda existente.
     * Exibe mensagem de sucesso ou erro após o redirecionamento.
     */
    @PostMapping("/cadastro")
    public String registerNewOccurrence(
            @RequestParam String trackingCode,
            @RequestParam TrackingStatus status,
            RedirectAttributes redirectAttributes) {

        try {
            OccurrenceCreateDTO dto = new OccurrenceCreateDTO();
            dto.setStatus(status);
            trackingService.registerNewOccurrence(trackingCode, dto);

            redirectAttributes.addFlashAttribute("sucessoCadastro", "Ocorrência registrada com sucesso!");

        } catch (ResourceNotFoundException | BusinessRuleException e) {
            redirectAttributes.addFlashAttribute("erroCadastro", e.getMessage());
            redirectAttributes.addFlashAttribute("trackingCode", trackingCode);
        }

        return "redirect:/cadastro";
    }

    /**
     * Exibe a página para cadastrar uma nova encomenda.
     */
    @GetMapping("/nova-encomenda")
    public String showNovaEncomendaPage(Model model) {
        model.addAttribute("orderDTO", new OrderCreateDTO());
        return "nova-encomenda";
    }

    /**
     * Cria uma nova encomenda com os dados fornecidos.
     * Exibe mensagem de sucesso ou erro após o redirecionamento.
     */
    @PostMapping("/nova-encomenda")
    public String createNewOrder(
            @ModelAttribute OrderCreateDTO orderDTO,
            RedirectAttributes redirectAttributes) {

        try {
            trackingService.createOrder(orderDTO);

            redirectAttributes.addFlashAttribute("sucesso", "Encomenda '" + orderDTO.getTrackingCode() + "' criada com sucesso!");

        } catch (BusinessRuleException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/nova-encomenda";
    }
}