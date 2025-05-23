package br.ifsp.demo.mapper;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.dto.asset.AssetResponseDTO;

import java.util.List;

public class AssetMapper {
    public static List<AssetResponseDTO> listToResponseDTO(List<Asset> assets) {
        return assets.stream()
                .map(asset -> new AssetResponseDTO(
                        asset.getId(),
                        asset.getName(),
                        asset.getAssetType(),
                        asset.getProfitability(),
                        asset.getMaturityDate()))
                .toList();
    }
}
