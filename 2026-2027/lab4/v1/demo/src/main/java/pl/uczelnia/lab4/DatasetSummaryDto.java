package pl.uczelnia.lab4;

public record DatasetSummaryDto(
        Long id,
        String code,
        String title,
        long observationCount,
        long reportCount
) {}
