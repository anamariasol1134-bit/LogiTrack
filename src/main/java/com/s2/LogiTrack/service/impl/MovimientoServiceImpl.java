package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.dto.request.MovimientoItemRequest;
import com.s2.LogiTrack.dto.request.MovimientoRequest;
import com.s2.LogiTrack.dto.response.MovimientoResponse;
import com.s2.LogiTrack.exception.ReglaNegocioException;
import com.s2.LogiTrack.exception.ResourceNotFoundException;
import com.s2.LogiTrack.exception.StockInsuficienteException;
import com.s2.LogiTrack.mapper.MovimientoMapper;
import com.s2.LogiTrack.model.*;
import com.s2.LogiTrack.model.enums.EstadoMovimiento;
import com.s2.LogiTrack.model.enums.TipoMovimiento;
import com.s2.LogiTrack.model.vista.VOcupacionBodega;
import com.s2.LogiTrack.repository.*;
import com.s2.LogiTrack.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ExistenciaRepository existenciaRepository;
    private final VOcupacionBodegaRepository ocupacionRepository;
    private final MovimientoMapper movimientoMapper;

    @Override
    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request, String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(request.tipo());
        movimiento.setUsuario(usuario);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setObservacion(request.observacion());
        movimiento.setEstado(EstadoMovimiento.REGISTRADO);

        Bodega bodegaOrigen = null;
        Bodega bodegaDestino = null;
        Proveedor proveedor = null;

        if (request.tipo() == TipoMovimiento.ENTRADA) {
            if (request.bodegaOrigenId() != null) throw new ReglaNegocioException("Una ENTRADA no puede tener bodega origen.");
            if (request.bodegaDestinoId() == null) throw new ReglaNegocioException("Una ENTRADA requiere bodega destino.");
            bodegaDestino = bodegaRepository.findById(request.bodegaDestinoId()).orElseThrow(() -> new ResourceNotFoundException("Bodega destino no encontrada"));

            if (request.proveedorId() != null) {
                proveedor = proveedorRepository.findById(request.proveedorId()).orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
                if (!proveedor.getActivo()) throw new ReglaNegocioException("El proveedor está inactivo.");
            }
        } else if (request.tipo() == TipoMovimiento.SALIDA) {
            if (request.bodegaOrigenId() == null) throw new ReglaNegocioException("Una SALIDA requiere bodega origen.");
            if (request.bodegaDestinoId() != null) throw new ReglaNegocioException("Una SALIDA no puede tener bodega destino.");
            if (request.proveedorId() != null) throw new ReglaNegocioException("Una SALIDA no permite proveedor.");
            bodegaOrigen = bodegaRepository.findById(request.bodegaOrigenId()).orElseThrow(() -> new ResourceNotFoundException("Bodega origen no encontrada"));
        } else if (request.tipo() == TipoMovimiento.TRANSFERENCIA) {
            if (request.bodegaOrigenId() == null || request.bodegaDestinoId() == null) throw new ReglaNegocioException("Transferencia requiere origen y destino.");
            if (request.bodegaOrigenId().equals(request.bodegaDestinoId())) throw new ReglaNegocioException("La bodega origen y destino deben ser diferentes.");
            if (request.proveedorId() != null) throw new ReglaNegocioException("Una TRANSFERENCIA no permite proveedor.");
            bodegaOrigen = bodegaRepository.findById(request.bodegaOrigenId()).orElseThrow(() -> new ResourceNotFoundException("Bodega origen no encontrada"));
            bodegaDestino = bodegaRepository.findById(request.bodegaDestinoId()).orElseThrow(() -> new ResourceNotFoundException("Bodega destino no encontrada"));
        }

        movimiento.setBodegaOrigen(bodegaOrigen);
        movimiento.setBodegaDestino(bodegaDestino);
        movimiento.setProveedor(proveedor);

        List<MovimientoItem> items = new ArrayList<>();
        int totalUnidadesDestino = 0;

        for (MovimientoItemRequest itemReq : request.items()) {
            Producto producto = productoRepository.findById(itemReq.productoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + itemReq.productoId()));

            if (!producto.getActivo()) throw new ReglaNegocioException("El producto " + producto.getNombre() + " está inactivo.");

            MovimientoItem item = new MovimientoItem();
            item.setMovimiento(movimiento);
            item.setProducto(producto);
            item.setCantidad(itemReq.cantidad());
            item.setPrecioUnitario(producto.getPrecio());
            items.add(item);

            if (request.tipo() == TipoMovimiento.ENTRADA || request.tipo() == TipoMovimiento.TRANSFERENCIA) {
                totalUnidadesDestino += itemReq.cantidad();
            }

            if (request.tipo() == TipoMovimiento.SALIDA || request.tipo() == TipoMovimiento.TRANSFERENCIA) {
                Existencia origen = existenciaRepository.findByBodegaIdAndProductoId(bodegaOrigen.getId(), producto.getId())
                        .orElseThrow(() -> new StockInsuficienteException("No hay existencia de " + producto.getNombre() + " en la bodega origen."));

                if (origen.getCantidad() < itemReq.cantidad()) {
                    throw new StockInsuficienteException("Stock insuficiente de " + producto.getNombre() + ". Solicitado: " + itemReq.cantidad() + ", Disponible: " + origen.getCantidad());
                }
                origen.setCantidad(origen.getCantidad() - itemReq.cantidad());
                existenciaRepository.save(origen);
            }

            if (request.tipo() == TipoMovimiento.ENTRADA || request.tipo() == TipoMovimiento.TRANSFERENCIA) {
                // --- CORRECCIÓN LAMBDA DESTINO ---
                final Bodega bodegaDestinoFinal = bodegaDestino;

                Existencia destino = existenciaRepository.findByBodegaIdAndProductoId(bodegaDestinoFinal.getId(), producto.getId())
                        .orElseGet(() -> {
                            Existencia nueva = new Existencia();
                            nueva.setBodega(bodegaDestinoFinal);
                            nueva.setProducto(producto);
                            nueva.setCantidad(0);
                            nueva.setVersion(0);
                            return nueva;
                        });
                destino.setCantidad(destino.getCantidad() + itemReq.cantidad());
                existenciaRepository.save(destino);
            }
        }

        if (request.tipo() == TipoMovimiento.ENTRADA || request.tipo() == TipoMovimiento.TRANSFERENCIA) {
            VOcupacionBodega ocupacion = ocupacionRepository.findById(bodegaDestino.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Datos de ocupación no encontrados"));

            long nuevoTotal = ocupacion.getUnidadesAlmacenadas() + totalUnidadesDestino;
            if (nuevoTotal > ocupacion.getCapacidad()) {
                throw new ReglaNegocioException("La bodega destino excede su capacidad. Capacidad: " + ocupacion.getCapacidad() + ", Ocupación actual: " + ocupacion.getUnidadesAlmacenadas() + ", Nuevas: " + totalUnidadesDestino);
            }
        }

        movimiento.setItems(items);
        Movimiento guardado = movimientoRepository.save(movimiento);
        return movimientoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar() {
        return movimientoRepository.findAll().stream()
                .map(movimientoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponse buscarPorId(Long id) {
        Movimiento mov = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));
        return movimientoMapper.toResponse(mov);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> buscarEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return movimientoRepository.findByFechaBetween(desde, hasta).stream()
                .map(movimientoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void anular(Long id, String motivo) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado"));

        if (movimiento.getEstado() == EstadoMovimiento.ANULADO) {
            throw new ReglaNegocioException("El movimiento ya está anulado.");
        }

        for (MovimientoItem item : movimiento.getItems()) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();

            if (movimiento.getTipo() == TipoMovimiento.ENTRADA || movimiento.getTipo() == TipoMovimiento.TRANSFERENCIA) {
                Existencia destino = existenciaRepository.findByBodegaIdAndProductoId(movimiento.getBodegaDestino().getId(), producto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Existencia destino no encontrada"));
                if (destino.getCantidad() < cantidad) {
                    throw new StockInsuficienteException("No se puede anular: el stock actual en destino es menor al del movimiento.");
                }
                destino.setCantidad(destino.getCantidad() - cantidad);
                existenciaRepository.save(destino);
            }

            if (movimiento.getTipo() == TipoMovimiento.SALIDA || movimiento.getTipo() == TipoMovimiento.TRANSFERENCIA) {
                // --- CORRECCIÓN LAMBDA ORIGEN ---
                final Bodega bodegaOrigenFinal = movimiento.getBodegaOrigen();

                Existencia origen = existenciaRepository.findByBodegaIdAndProductoId(bodegaOrigenFinal.getId(), producto.getId())
                        .orElseGet(() -> {
                            Existencia nueva = new Existencia();
                            nueva.setBodega(bodegaOrigenFinal);
                            nueva.setProducto(producto);
                            nueva.setCantidad(0);
                            nueva.setVersion(0);
                            return nueva;
                        });
                origen.setCantidad(origen.getCantidad() + cantidad);
                existenciaRepository.save(origen);
            }
        }

        movimiento.setEstado(EstadoMovimiento.ANULADO);
        movimiento.setFechaAnulacion(LocalDateTime.now());
        movimiento.setMotivoAnulacion(motivo);
        movimientoRepository.save(movimiento);
    }
}