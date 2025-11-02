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
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Importe este

@Controller // Continua sendo @Controller
public class WebController {

    private final TrackingService trackingService;

    public WebController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    /**
     * Método 1: Página principal.
     * Redireciona / para a tela de consulta.
     */
    @GetMapping("/")
    public String showHomePage() {
        return "redirect:/consulta"; // Redireciona para a tela de consulta
    }

    /**
     * Método 2: Mostrar e Processar a "Consulta de Status".
     * Se 'trackingCode' for nulo, apenas mostra a página.
     * Se 'trackingCode' existir, processa a busca.
     */
    @GetMapping("/consulta")
    public String getTrackingDetails(
            @RequestParam(name = "trackingCode", required = false) String trackingCode,
            Model model) {

        // Só executa a lógica se o usuário realmente enviou um código
        if (trackingCode != null && !trackingCode.isEmpty()) {
            try {
                OrderDetailsDTO details = trackingService.getTrackingDetails(trackingCode);
                model.addAttribute("timeline", details);
            } catch (ResourceNotFoundException e) {
                model.addAttribute("erroConsulta", e.getMessage());
            }
        }

        // Renderiza a página 'consulta.html' (vazia ou com dados)
        return "consulta";
    }

    /**
     * Método 3: Apenas MOSTRA a página de "Cadastro de Ocorrência".
     */
    @GetMapping("/cadastro")
    public String showCadastroPage() {
        // Apenas renderiza a página 'cadastro.html'
        return "cadastro";
    }

    /**
     * Método 4: Processa o formulário de "Cadastro de Ocorrência".
     */
    @PostMapping("/cadastro")
    public String registerNewOccurrence(
            @RequestParam String trackingCode,
            @RequestParam TrackingStatus status,
            RedirectAttributes redirectAttributes) { // Usamos RedirectAttributes

        try {
            OccurrenceCreateDTO dto = new OccurrenceCreateDTO();
            dto.setStatus(status);
            trackingService.registerNewOccurrence(trackingCode, dto);

            // "Flash Attribute" envia a mensagem MESMO após o redirect
            redirectAttributes.addFlashAttribute("sucessoCadastro", "Ocorrência registrada com sucesso!");

        } catch (ResourceNotFoundException | BusinessRuleException e) {
            // Se der erro, envia a mensagem de erro
            redirectAttributes.addFlashAttribute("erroCadastro", e.getMessage());
            // Também reenvia os dados do formulário para o usuário não perder
            redirectAttributes.addFlashAttribute("trackingCode", trackingCode);
        }

        // Recarrega a página de cadastro (GET /cadastro)
        return "redirect:/cadastro";
    }

    /**
     * Método 5: Apenas MOSTRA a página "Cadastrar Nova Encomenda".
     */
    @GetMapping("/nova-encomenda")
    public String showNovaEncomendaPage(Model model) {
        // Envia um objeto DTO vazio para o Thymeleaf
        // para que o formulário possa "ligar-se" (bind) a ele.
        model.addAttribute("orderDTO", new OrderCreateDTO());
        return "nova-encomenda";
    }

    /**
     * Método 6: Processa o formulário de "Cadastro de Encomenda".
     * Chama o service que já criamos para isso.
     */
    @PostMapping("/nova-encomenda")
    public String createNewOrder(
            // @ModelAttribute liga todos os campos do formulário
            // diretamente no objeto DTO. É mais limpo que @RequestParam.
            @ModelAttribute OrderCreateDTO orderDTO,
            RedirectAttributes redirectAttributes) {

        try {
            // Chama o método do service que você já implementou!
            trackingService.createOrder(orderDTO);

            // Se der certo, envia mensagem de sucesso
            redirectAttributes.addFlashAttribute("sucesso", "Encomenda '" + orderDTO.getTrackingCode() + "' criada com sucesso!");

        } catch (BusinessRuleException e) {
            // Se der erro (ex: código já existe), envia a mensagem de erro
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        // Redireciona de volta para a página de cadastro (limpa)
        return "redirect:/nova-encomenda";
    }
}