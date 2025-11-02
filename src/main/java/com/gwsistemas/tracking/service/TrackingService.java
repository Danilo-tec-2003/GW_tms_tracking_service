package com.gwsistemas.tracking.service;

import com.gwsistemas.tracking.dto.input.OccurrenceCreateDTO;
import com.gwsistemas.tracking.dto.input.OrderCreateDTO;
import com.gwsistemas.tracking.dto.output.OccurrenceDTO;
import com.gwsistemas.tracking.dto.output.OrderDetailsDTO;
import com.gwsistemas.tracking.enums.TrackingStatus;
import com.gwsistemas.tracking.exception.BusinessRuleException;
import com.gwsistemas.tracking.exception.ResourceNotFoundException;
import com.gwsistemas.tracking.mapper.OccurrenceMapper;
import com.gwsistemas.tracking.mapper.OrderMapper;
import com.gwsistemas.tracking.model.Occurrence;
import com.gwsistemas.tracking.model.Order;
import com.gwsistemas.tracking.repository.OccurrenceRepository;
import com.gwsistemas.tracking.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

/**
 * Serviço responsável pelo gerenciamento de encomendas e ocorrências.
 * Contém lógica de negócio, validações de status e manipulação de dados.
 */

@Service
public class TrackingService {


    private final OrderRepository orderRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final OrderMapper orderMapper;
    private final OccurrenceMapper occurrenceMapper;

    public TrackingService(OrderRepository orderRepository, OccurrenceRepository occurrenceRepository, OrderMapper orderMapper, OccurrenceMapper occurrenceMapper) {
        this.orderRepository = orderRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.orderMapper = orderMapper;
        this.occurrenceMapper = occurrenceMapper;
    }

    /**
     * Registra uma nova ocorrência (evento de rastreio) para uma encomenda.
     *
     * @param trackingCode Código de rastreio da encomenda.
     * @param dto DTO com o novo status da ocorrência.
     * @return DTO da ocorrência criada.
     * @throws ResourceNotFoundException se a encomenda não existir.
     * @throws BusinessRuleException se uma regra de negócio for violada.
     */
    @Transactional
    public OccurrenceDTO registerNewOccurrence(String trackingCode, OccurrenceCreateDTO dto) {
        Order order = findOrderByTrackingCode(trackingCode);
        Optional<Occurrence> latestOccurrence = findLatestOccurrence(order);

        validateBusinessRules(latestOccurrence, dto);
        Occurrence saved = createAndSaveOccurrence(order, dto);

        return occurrenceMapper.toDTO(saved);
    }


    /**
     * Busca uma encomenda pelo código de rastreio.
     */
    private Order findOrderByTrackingCode(String trackingCode) {
        return orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Encomenda não encontrada."));
    }


    /**
     * Retorna a última ocorrência registrada para a encomenda
     */
    private Optional<Occurrence> findLatestOccurrence(Order order) {
        return order.getOccurrences()
                .stream()
                .max(Comparator.comparing(Occurrence::getOccurrenceTimestamp));
    }


    /**
     * Aplica as regras de negócio que controlam o fluxo de status:
     * - Impede novos eventos após "ENTREGUE".
     * - Após "NÃO ENTREGUE", apenas "SAÍDA PARA ENTREGA" é permitido.
     */
    private void validateBusinessRules(Optional<Occurrence> latestOccurrenceOpt, OccurrenceCreateDTO dto) {
        if (latestOccurrenceOpt.isEmpty()) {
            return;
        }

        TrackingStatus latestStatus = latestOccurrenceOpt.get().getStatus();
        TrackingStatus newStatus = dto.getStatus();

        if (latestStatus == TrackingStatus.ENTREGUE) {
            throw new BusinessRuleException("A encomenda já foi marcada como 'ENTREGUE'.");
        }

        if (latestStatus == TrackingStatus.NAO_ENTREGUE
                && newStatus != TrackingStatus.SAIDA_PARA_ENTREGA) {
            throw new BusinessRuleException("Após 'NÃO ENTREGUE', o único status permitido é 'SAÍDA PARA ENTREGA'.");
        }
    }

    /**
     * Cria uma nova ocorrência com os dados do DTO,
     * associa à encomenda e salva no banco de dados.
     */
    private Occurrence createAndSaveOccurrence(Order order, OccurrenceCreateDTO dto) {
        Occurrence occurrence = occurrenceMapper.toEntity(dto);
        occurrence.setOrder(order);
        occurrence.setOccurrenceTimestamp(LocalDateTime.now());
        return occurrenceRepository.save(occurrence);
    }

    /**
     * Consulta o status atual e a timeline completa de uma encomenda.
     *
     * @param trackingCode Código de rastreio da encomenda.
     * @return DTO com detalhes da encomenda e lista de ocorrências ordenada.
     * @throws ResourceNotFoundException se a encomenda não existir.
     */
    public OrderDetailsDTO getTrackingDetails(String trackingCode) {

        Order order = findOrderByTrackingCode(trackingCode);
        OrderDetailsDTO dto = orderMapper.toDetailsDTO(order);
        dto.getOccurrences().sort(
                Comparator.comparing(OccurrenceDTO::getOccurrenceTimestamp).reversed()
        );

        return dto;
    }

    /**
     * Cria uma nova encomenda.
     *
     * @param dto DTO com os dados da nova encomenda.
     * @return DTO da encomenda criada.
     * @throws BusinessRuleException se o código de rastreio já existir.
     */
    @Transactional
    public OrderDetailsDTO createOrder(OrderCreateDTO dto) {
        Optional<Order> existingOrderOpt = orderRepository.findByTrackingCode(dto.getTrackingCode());

        if (existingOrderOpt.isPresent()) {
            throw new BusinessRuleException("Já existe uma encomenda cadastrada com o código de rastreio: " + dto.getTrackingCode());
        }

        Order newOrder = orderMapper.toEntity(dto);
        Order savedOrder = orderRepository.save(newOrder);

        return orderMapper.toDetailsDTO(savedOrder);
    }

}
